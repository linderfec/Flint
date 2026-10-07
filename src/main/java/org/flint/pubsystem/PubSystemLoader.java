package org.flint.pubsystem;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * pubSystem 模块加载器
 * 扫描 run/pubSystem/ 目录，动态加载 jar 并初始化模块
 * 模块类需实现 PubModule 并标注 @PUBCOM（未标注时回退到 SPI 注册）
 *
 * 用法：
 *   PubSystemLoader loader = new PubSystemLoader(pubSystemDir, inst);
 *   loader.loadAll();
 *   loader.getModule("logger");
 *   loader.shutdownAll();
 */
public class PubSystemLoader {

    private final Path moduleDir;
    private final Instrumentation inst;
    private final List<PubModule> modules = new ArrayList<>();

    public PubSystemLoader(String moduleDir, Instrumentation inst) {
        this.moduleDir = Paths.get(moduleDir);
        this.inst = inst;
    }

    /** 扫描目录并加载所有模块 jar */
    public void loadAll() {
        if (!Files.exists(moduleDir)) {
            System.out.println("[PubSystem] 目录不存在: " + moduleDir);
            return;
        }

        System.out.println("[PubSystem] 扫描模块目录: " + moduleDir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(moduleDir, "*.jar")) {
            for (Path jarPath : stream) {
                loadJar(jarPath);
            }
        } catch (Exception e) {
            System.err.println("[PubSystem] 扫描失败: " + e.getMessage());
        }
    }

    /**
     * 加载单个 jar
     * 优先扫描 @PUBCOM 注解发现模块，没有注解模块时回退到 SPI（META-INF/services）以保持兼容
     */
    private void loadJar(Path jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath.toFile());
            inst.appendToSystemClassLoaderSearch(jarFile);

            try (URLClassLoader loader = new URLClassLoader(
                    new URL[]{jarPath.toUri().toURL()},
                    PubSystemLoader.class.getClassLoader())) {

                int loaded = loadAnnotated(jarFile, loader);
                if (loaded == 0) {
                    loaded = loadBySpi(loader);
                }

                if (loaded == 0) {
                    System.out.println("[PubSystem] 未发现模块: " + jarPath.getFileName());
                }
            }

        } catch (Exception e) {
            System.err.println("[PubSystem] 加载失败: " + jarPath.getFileName() + " - " + e.getMessage());
        }
    }

    /** 扫描 jar 内带 @PUBCOM 注解的类并实例化 */
    private int loadAnnotated(JarFile jarFile, ClassLoader loader) {
        int loaded = 0;

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().endsWith(".class")) continue;

            String className = entry.getName()
                    .replace('/', '.')
                    .replace(".class", "");

            if (className.equals("module-info") || className.equals("package-info")) continue;

            try {
                Class<?> clazz = loader.loadClass(className);
                PUBCOM annotation = clazz.getAnnotation(PUBCOM.class);
                if (annotation == null) continue;

                if (!PubModule.class.isAssignableFrom(clazz)) {
                    System.err.println("[PubSystem] @" + annotation.value()
                            + " 类未实现 PubModule 接口: " + className);
                    continue;
                }

                instantiate(clazz, annotation.value(), className);
                loaded++;

            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
            } catch (Exception | LinkageError e) {
                // 单个模块出错不应中断整个 jar 的扫描
                System.err.println("[PubSystem] 模块初始化失败: " + className + " - " + e);
            }
        }
        return loaded;
    }

    /** 兼容旧的 SPI 注册方式（META-INF/services） */
    private int loadBySpi(ClassLoader loader) {
        int loaded = 0;
        ServiceLoader<PubModule> serviceLoader = ServiceLoader.load(PubModule.class, loader);
        for (PubModule module : serviceLoader) {
            if (isLoaded(module.getClass())) continue;
            module.onLoad(inst);
            modules.add(module);
            loaded++;
            System.out.println("[PubSystem] 已加载(SPI): " + module.getName() + " v" + module.getVersion());
        }
        return loaded;
    }

    /** 实例化模块、回调 onLoad 并登记 */
    private void instantiate(Class<?> clazz, String label, String className) throws ReflectiveOperationException {
        if (isLoaded(clazz)) return;

        PubModule module = (PubModule) clazz.getDeclaredConstructor().newInstance();
        module.onLoad(inst);
        modules.add(module);
        System.out.println("[PubSystem] 已加载: " + module.getName() + " v" + module.getVersion()
                + " (" + label + " -> " + className + ")");
    }

    /** 防止注解扫描与 SPI 重复加载同一个模块 */
    private boolean isLoaded(Class<?> clazz) {
        for (PubModule m : modules) {
            if (m.getClass().equals(clazz)) return true;
        }
        return false;
    }

    /** 获取指定名称的模块 */
    public PubModule getModule(String name) {
        for (PubModule m : modules) {
            if (m.getName().equals(name)) return m;
        }
        return null;
    }

    /** 获取所有已加载的模块（不可修改） */
    public List<PubModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    /** 卸载指定名称的模块 */
    public boolean unloadModule(String name) {
        Iterator<PubModule> it = modules.iterator();
        while (it.hasNext()) {
            PubModule m = it.next();
            if (m.getName().equals(name)) {
                m.onUnload();
                it.remove();
                System.out.println("[PubSystem] 已卸载: " + name);
                return true;
            }
        }
        return false;
    }

    /** 卸载所有模块 */
    public void shutdownAll() {
        for (PubModule m : modules) {
            m.onUnload();
        }
        modules.clear();
        System.out.println("[PubSystem] 所有模块已卸载");
    }
}
