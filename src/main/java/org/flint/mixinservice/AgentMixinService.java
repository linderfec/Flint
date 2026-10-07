package org.flint.mixinservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinServiceAbstract;
import org.spongepowered.asm.transformers.MixinClassReader;

/**
 * Mixin 服务实现，用于「纯 Java Agent」环境（没有 launchwrapper / ModLauncher 之类的宿主）。
 *
 * <p>Flint 通过 {@code -javaagent} 挂载，JVM 里不存在 Mixin 自带的
 * {@code MixinServiceLaunchWrapper}（需要 launchwrapper）或
 * {@code MixinServiceModLauncher}（需要 ModLauncher），因此必须自己提供一个
 * {@link org.spongepowered.asm.service.IMixinService}，否则 Mixin 启动时会报
 * <tt>No mixin host service is available</tt>。</p>
 *
 * <p>类的加载/字节码读取全部走系统类加载器（Agent JAR 会被 JVM 追加到系统类路径上，
 * 游戏类也在同一个类加载器里）。</p>
 */
public class AgentMixinService extends MixinServiceAbstract
        implements IClassProvider, IClassBytecodeProvider, ITransformerProvider, IClassTracker {

    private static final String PLATFORM_AGENT_CLASS = AgentPlatform.class.getName();

    /** 本服务自己的类加载器（系统类加载器） */
    private final ClassLoader classLoader = AgentMixinService.class.getClassLoader();

    /** Mixin 标记为不可加载的类（仅作记录，Agent 环境下没有可用的拦截点） */
    private final Set<String> invalidClasses = ConcurrentHashMap.newKeySet();

    /** 由 Agent 显式排除的旧式转换器名称（Agent 环境没有转换器链，保留记录即可） */
    private final Set<String> excludedTransformers = ConcurrentHashMap.newKeySet();

    @Override
    public String getName() {
        return "FlintAgent";
    }

    @Override
    public boolean isValid() {
        // Agent 环境永远可用：Flint 就是宿主本身
        return true;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.singletonList(PLATFORM_AGENT_CLASS);
    }

    /* ---------------- 容器 / 资源 ---------------- */

    @Override
    public IContainerHandle getPrimaryContainer() {
        return getAgentContainer();
    }

    /**
     * Flint Agent 自身所在的容器（Agent JAR）
     *
     * @return 容器句柄
     */
    public static IContainerHandle getAgentContainer() {
        try {
            URI uri = AgentMixinService.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (uri != null) {
                return new ContainerHandleURI(uri);
            }
        } catch (URISyntaxException | RuntimeException ex) {
            System.err.println("[Flint-Mixin] 无法解析 Agent 容器 URI: " + ex);
        }
        return new ContainerHandleVirtual("flint-agent");
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.classLoader.getResourceAsStream(name);
    }

    /* ---------------- 组件提供者 ---------------- */

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return this;
    }

    @Override
    public IClassTracker getClassTracker() {
        return this;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    /* ---------------- IClassProvider ---------------- */

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return Class.forName(name, false, this.classLoader);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, this.classLoader);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, this.classLoader);
    }

    @Override
    @Deprecated
    public URL[] getClassPath() {
        List<URL> classPath = new ArrayList<URL>();
        for (String entry : System.getProperty("java.class.path", "").split(File.pathSeparator)) {
            if (entry.isEmpty()) {
                continue;
            }
            try {
                classPath.add(new File(entry).toURI().toURL());
            } catch (IOException ex) {
                // 跳过无法解析的 classpath 条目
            }
        }
        return classPath.toArray(new URL[0]);
    }

    /* ---------------- IClassBytecodeProvider ---------------- */

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return this.getClassNode(name, false, ClassReader.EXPAND_FRAMES);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        return this.getClassNode(name, runTransformers, ClassReader.EXPAND_FRAMES);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers, int readerFlags)
            throws ClassNotFoundException, IOException {
        // Agent 环境没有 classloader 级别的转换器链，直接读取原始字节即可。
        // Mixin 只用它来分析目标类 / Mixin 类的结构，原始字节正是想要的。
        byte[] classBytes = this.getClassBytes(name);
        ClassNode classNode = new ClassNode();
        new MixinClassReader(classBytes, name).accept(classNode, readerFlags);
        return classNode;
    }

    private byte[] getClassBytes(String name) throws ClassNotFoundException {
        String resource = name.replace('.', '/') + ".class";
        InputStream stream = this.classLoader.getResourceAsStream(resource);
        if (stream == null) {
            throw new ClassNotFoundException(String.format("The specified class '%s' was not found", name));
        }

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int read;
            while ((read = stream.read(chunk)) > 0) {
                buffer.write(chunk, 0, read);
            }
            return buffer.toByteArray();
        } catch (IOException ex) {
            throw new ClassNotFoundException(String.format("Could not read class '%s'", name), ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }

    /* ---------------- ITransformerProvider ---------------- */

    @Override
    public Collection<ITransformer> getTransformers() {
        // Agent 环境没有旧式转换器链，返回空列表（MixinEnvironment 需要 List 类型）
        return Collections.<ITransformer>emptyList();
    }

    @Override
    public Collection<ITransformer> getDelegatedTransformers() {
        return Collections.<ITransformer>emptyList();
    }

    @Override
    public void addTransformerExclusion(String name) {
        this.excludedTransformers.add(name);
    }

    /* ---------------- IClassTracker ---------------- */

    @Override
    public void registerInvalidClass(String className) {
        this.invalidClasses.add(className);
    }

    @Override
    public boolean isClassLoaded(String className) {
        // Instrumentation 无法枚举已加载的类，返回 false 表示「没有过早加载」
        return this.invalidClasses.contains(className);
    }

    @Override
    public String getClassRestrictions(String className) {
        return "";
    }
}
