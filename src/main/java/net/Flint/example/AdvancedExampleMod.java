package net.Flint.example;

import net.Flint.api.FlintAPI;
import net.Flint.modloader.Mod;
import net.Flint.modloader.IFlintMod;
import static net.Flint.logs.logger.logger;

/**
 * 扩展示例模组 - 演示如何使用Flint API系统
 */
@Mod(value = "advanced_example_mod", name = "Advanced Example Mod", version = "1.0.0", description = "这是一个演示Flint API使用的高级示例模组")
public class AdvancedExampleMod implements IFlintMod {
    
    @Override
    public void onInitialize() {
        logger.info("Advanced Example Mod is initializing...");
        
        // 使用Flint API
        logger.info("Flint API Version: " + FlintAPI.getVersion());
        logger.info("Registered mods count: " + FlintAPI.getModCount());
        
        logger.info("Advanced Example Mod initialized successfully!");
    }
    
    @Override
    public void onPreStartup() {
        logger.info("Advanced Example Mod is executing pre-startup logic...");
    }
    
    @Override
    public void onStartup() {
        logger.info("Advanced Example Mod is executing startup logic...");
    }
    
    @Override
    public void onPostStartup() {
        logger.info("Advanced Example Mod is executing post-startup logic...");
    }
    
    @Override
    public void onInGame() {
        // 在游戏中定期执行的逻辑
    }
    
    @Override
    public void onPreShutdown() {
        logger.info("Advanced Example Mod is executing pre-shutdown logic...");
    }
    
    @Override
    public void onShutdown() {
        logger.info("Advanced Example Mod is executing shutdown logic...");
    }
}