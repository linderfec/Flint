package net.Flint;

import net.Flint.logs.logger;
import net.Flint.mixin.MixinInitializer;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.Instrumentation;

/**
 * Flint Agent类 - Java Agent实现类
 * 用于在JVM启动时或运行时加载Flint Mod Loader
 */
public class FlintAgent {
    /**
     * ASM字节码操作框架版本常量
     */
    public static final int ASM_VERSION = Opcodes.ASM9;

    /**
     * JVM启动时预加载方法
     * 当Java Agent在JVM启动时被加载时调用此方法
     * 
     * @param agentArgs 代理参数
     * @param inst Instrumentation实例，用于操作字节码
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("Flint Mod Loader version " + FlintLauncherMain.FLINTVERSION + " is initializing...");
        
        // 初始化Mixin系统
        MixinInitializer.initialize();
    }

    /**
     * JVM运行时动态加载方法
     * 当Java Agent在JVM运行时被动态加载时调用此方法
     * 
     * @param agentArgs 代理参数
     * @param inst Instrumentation实例，用于操作字节码
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        logger.info("Flint loaded in running JVM!");
        premain(agentArgs, inst);
    }
}