package org.flint.pubsystem;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 PubSystemLoader 的两种发现方式：
 *   1. 优先扫描 @PUBCOM 注解
 *   2. 没有注解模块时回退到 SPI
 */
class PubSystemLoaderTest {

    /** 只有 @PUBCOM，没有注册 SPI */
    @PUBCOM("annotated")
    public static class AnnotatedModule implements PubModule {
        static boolean loaded = false;
        static boolean unloaded = false;

        @Override public String getName()    { return "annotated"; }
        @Override public String getVersion() { return "1.0.0"; }
        @Override public void onLoad(Instrumentation inst) { loaded = true; }
        @Override public void onUnload() { unloaded = true; }
    }

    /** 只注册 SPI，没有 @PUBCOM */
    public static class SpiModule implements PubModule {
        static boolean loaded = false;

        @Override public String getName()    { return "spi"; }
        @Override public String getVersion() { return "2.0.0"; }
        @Override public void onLoad(Instrumentation inst) { loaded = true; }
        @Override public void onUnload() { }
    }

    /** 同时带 @PUBCOM 和 SPI 注册，用于验证不会重复加载 */
    @PUBCOM("both")
    public static class BothModule implements PubModule {
        static int loadCount = 0;

        @Override public String getName()    { return "both"; }
        @Override public String getVersion() { return "3.0.0"; }
        @Override public void onLoad(Instrumentation inst) { loadCount++; }
        @Override public void onUnload() { }
    }

    /** 找不到无参构造函数，应当被跳过而不是让整个 jar 失败 */
    @PUBCOM("broken")
    public static class NoDefaultCtor implements PubModule {
        public NoDefaultCtor(String ignored) { }
        @Override public String getName()    { return "broken"; }
        @Override public String getVersion() { return "0"; }
        @Override public void onLoad(Instrumentation inst) { }
        @Override public void onUnload() { }
    }

    @Test
    void loadsAnnotatedModule() throws Exception {
        AnnotatedModule.loaded = false;

        Path jar = jarOf(AnnotatedModule.class);
        PubSystemLoader loader = new PubSystemLoader(jar.getParent().toString(), fakeInstrumentation());
        loader.loadAll();

        PubModule module = loader.getModule("annotated");
        assertNotNull(module, "@PUBCOM 模块应当被加载");
        assertTrue(AnnotatedModule.loaded, "onLoad 应当被调用");
        assertEquals("1.0.0", module.getVersion());

        loader.shutdownAll();
        assertTrue(AnnotatedModule.unloaded, "onUnload 应当被调用");
    }

    @Test
    void fallsBackToSpiWhenNoAnnotation() throws Exception {
        SpiModule.loaded = false;

        Path jar = jarOf(SpiModule.class, "META-INF/services/org.flint.pubsystem.PubModule",
                SpiModule.class.getName());
        PubSystemLoader loader = new PubSystemLoader(jar.getParent().toString(), fakeInstrumentation());
        loader.loadAll();

        PubModule module = loader.getModule("spi");
        assertNotNull(module, "没有 @PUBCOM 时应当回退到 SPI");
        assertTrue(SpiModule.loaded, "SPI 模块的 onLoad 应当被调用");
    }

    @Test
    void annotationWinsAndNoDuplicateLoad() throws Exception {
        BothModule.loadCount = 0;

        Path jar = jarOf(BothModule.class, "META-INF/services/org.flint.pubsystem.PubModule",
                BothModule.class.getName());
        PubSystemLoader loader = new PubSystemLoader(jar.getParent().toString(), fakeInstrumentation());
        loader.loadAll();

        assertEquals(1, BothModule.loadCount, "注解与 SPI 不应重复加载同一个模块");
        assertEquals(1, loader.getModules().size(), "只应登记一个模块");
    }

    @Test
    void skipsClassWithoutPubModuleContract() throws Exception {
        // 注解了但没实现 PubModule 的类不应导致整个 jar 失败
        Path jar = jarOf(AnnotatedModule.class, NoDefaultCtor.class);
        PubSystemLoader loader = new PubSystemLoader(jar.getParent().toString(), fakeInstrumentation());
        loader.loadAll();

        List<PubModule> modules = loader.getModules();
        assertEquals(1, modules.size(), "有效模块仍应被加载，坏类只被跳过");
        assertNull(loader.getModule("broken"), "无法实例化的模块不应被登记");
    }

    // ------------------------------------------------------------------

    /** Instrumentation 是接口，用动态代理造一个空实现（测试里不需要真实 agent） */
    private static Instrumentation fakeInstrumentation() {
        return (Instrumentation) Proxy.newProxyInstance(
                PubSystemLoaderTest.class.getClassLoader(),
                new Class<?>[]{Instrumentation.class},
                (proxy, method, args) -> {
                    Class<?> rt = method.getReturnType();
                    if (rt == boolean.class) return false;
                    if (rt == long.class) return 0L;
                    if (rt == int.class) return 0;
                    return null;
                });
    }

    /** 把指定类的 .class 打成 jar，放在临时目录里 */
    private static Path jarOf(Class<?>... classes) throws Exception {
        return jarOf(classes, null, null);
    }

    private static Path jarOf(Class<?> clazz, String serviceFile, String serviceContent) throws Exception {
        return jarOf(new Class<?>[]{clazz}, serviceFile, serviceContent);
    }

    private static Path jarOf(Class<?>[] classes, String serviceFile, String serviceContent) throws Exception {
        Path dir = Files.createTempDirectory("pubsystem-test");
        Path jar = dir.resolve("modules.jar");

        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            for (Class<?> c : classes) {
                String entry = c.getName().replace('.', '/') + ".class";
                out.putNextEntry(new JarEntry(entry));
                out.write(classBytes(c));
                out.closeEntry();
            }
            if (serviceFile != null) {
                out.putNextEntry(new JarEntry(serviceFile));
                out.write(serviceContent.getBytes("UTF-8"));
                out.closeEntry();
            }
        }
        return jar;
    }

    private static byte[] classBytes(Class<?> clazz) throws Exception {
        String resource = "/" + clazz.getName().replace('.', '/') + ".class";
        try (InputStream in = clazz.getResourceAsStream(resource)) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            int n;
            while ((n = in.read(chunk)) > 0) buf.write(chunk, 0, n);
            return buf.toByteArray();
        }
    }
}
