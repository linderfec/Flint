package net.Flint;

import net.Flint.logs.logger;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.Instrumentation;

public class FlintAgent {
    public static final int ASM_VERSION = Opcodes.ASM9;

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("Flint Agent loaded successfully!");
        logger.info("Flint Mod Loader version " + FlintLauncherMain.FLINTVERSION + " is initializing...");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        logger.info("Flint Agent loaded in running JVM!");
        premain(agentArgs, inst);
    }
}