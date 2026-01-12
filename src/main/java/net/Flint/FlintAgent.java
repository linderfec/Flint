package net.Flint;

import net.Flint.logs.logger;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;

public class FlintAgent {
    //ASM9 版本 用于字节的控制
    public static final int ASM_VERSION = Opcodes.ASM9;

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("Flint Agent loaded successfully!");
        logger.info("Flint Mod Loader version " + FlintLauncherMain.FLINTVERSION + " is initializing...");
        
        // 在这里可以添加字节码转换逻辑
        // inst.addTransformer(new YourClassFileTransformer(), true);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        logger.info("Flint Agent loaded in running JVM!");
        premain(agentArgs, inst);
    }
}