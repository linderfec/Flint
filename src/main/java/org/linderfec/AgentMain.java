package org.linderfec;

import org.linderfec.dirpath.DirPath;
import org.linderfec.modsrt.ModsrtLoader;
import org.linderfec.pacagemanager.PackageMain;
import org.linderfec.pubsystem.PubSystemLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.lang.instrument.Instrumentation;
import java.nio.file.Paths;

public class AgentMain {
    private static PubSystemLoader pubSystem;
    private static ModsrtLoader modsrt;

    public static void premain(String agentArgs, Instrumentation inst) {
        PackageMain.pacagent();

        System.out.println("[Flint] Agent 启动成功，参数: " + agentArgs);

        String gameDir = null;
        String pubSystemDir = null;

        if (agentArgs != null) {
            for (String arg : agentArgs.split(",")) {
                if (arg.startsWith("--gamedir=")) {
                    gameDir = arg.substring("--gamedir=".length());
                } else if (arg.startsWith("--pubsystem=")) {
                    pubSystemDir = arg.substring("--pubsystem=".length());
                }
            }
        }

        if (gameDir == null) {
            gameDir = Paths.get("").toAbsolutePath().toString();
        }
        DirPath.dirpath(new String[]{gameDir});

        if (pubSystemDir == null) {
            pubSystemDir = Paths.get("run", "pubSystem").toString();
        }

        pubSystem = new PubSystemLoader(pubSystemDir, inst);
        pubSystem.loadAll();

        modsrt = new ModsrtLoader(DirPath.modstader(), inst);
        modsrt.loadAll();

        inst.addTransformer(new MainMethodTransformer(), true);
        inst.addTransformer(new TitleScreenTransformer(), true);

        MixinBootstrap.init();
        Mixins.addConfiguration("flint.mixins.json");
    }

    /** 供外部获取 PubSystemLoader，用于管理模块 */
    public static PubSystemLoader getPubSystem() {
        return pubSystem;
    }

    /** 供外部获取 ModsrtLoader，用于管理 Mod */
    public static ModsrtLoader getModsrt() {
        return modsrt;
    }
}