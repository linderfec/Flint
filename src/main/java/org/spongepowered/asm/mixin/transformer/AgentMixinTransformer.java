package org.spongepowered.asm.mixin.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * 把 Mixin 转换器接到 Java Agent（{@code java.lang.instrument}）上的桥接类。
 *
 * <p>Mixin 官方只提供了三种宿主：launchwrapper（{@code Proxy}）、ModLauncher
 * （{@code MixinLaunchPluginLegacy}）以及热替换 Agent（{@code MixinAgent}，
 * 只用于重转换，不会在类加载时应用 mixin）。Flint 是纯 {@code -javaagent}
 * 方式启动的 vanilla 客户端，没有上面任何一个宿主，所以需要这个
 * {@link ClassFileTransformer} 在类加载时调用 {@link MixinTransformer}。</p>
 *
 * <p>本类必须位于 {@code org.spongepowered.asm.mixin.transformer} 包内，
 * 因为 {@link MixinTransformer} 是包私有的。</p>
 */
public class AgentMixinTransformer implements ClassFileTransformer {

    /**
     * 跳过转换的类名前缀：JDK 平台类、Mixin 自身、Mixin 的依赖库，
     * 以及 Mixin 包下的类（加载它们会触发 IllegalClassLoadError）。
     */
    private static final String[] IGNORED_PREFIXES = {
        "java/", "javax/", "jdk/", "sun/", "com/sun/",
        "org/spongepowered/",
            "org/flint/mixin/",
        "org/objectweb/asm/",
        "com/google/gson/",
        "com/google/common/"
    };

    /**
     * 单例。{@link MixinTransformer} 持有可变状态（TreeTransformer 会缓存
     * ClassReader），并且构造时会注册为环境的活动转换器，所以只能有一个实例。
     */
    private static volatile MixinTransformer transformer;

    /**
     * 转换锁：Mixin 的写回逻辑不是线程安全的，串行化字节码转换以避免竞争。
     */
    private static final Object transformLock = new Object();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className == null || classfileBuffer == null || AgentMixinTransformer.isIgnored(className)) {
            return null;
        }

        // Mixin 用点号分隔的类名（与 launchwrapper 的约定一致）
        String dottedName = className.replace('/', '.');

        synchronized (AgentMixinTransformer.transformLock) {
            try {
                byte[] result = AgentMixinTransformer.getTransformer().transformClassBytes(dottedName, dottedName, classfileBuffer);
                // 与输入数组相同表示没有改动，返回 null 表示「不修改该类」
                return result != null && result != classfileBuffer ? result : null;
            } catch (Throwable th) {
                // JVM 会吞掉转换器抛出的异常并照常加载原字节码，所以必须自己打印，
                // 否则 mixin 失败会完全无声无息
                System.err.println("[Flint-Mixin] 转换 " + dottedName + " 时发生错误:");
                th.printStackTrace(System.err);
                return null;
            }
        }
    }

    private static MixinTransformer getTransformer() {
        MixinTransformer instance = AgentMixinTransformer.transformer;
        if (instance == null) {
            synchronized (AgentMixinTransformer.class) {
                instance = AgentMixinTransformer.transformer;
                if (instance == null) {
                    AgentMixinTransformer.transformer = instance = new MixinTransformer();
                }
            }
        }
        return instance;
    }

    private static boolean isIgnored(String className) {
        for (String prefix : AgentMixinTransformer.IGNORED_PREFIXES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
