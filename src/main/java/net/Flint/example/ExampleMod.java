package net.Flint.example;

import net.Flint.modloader.Mod;
import net.Flint.modloader.IFlintMod;
import static net.Flint.logs.logger.logger;

/**
 * 示例模组 - 演示如何使用Flint模组系统
 */
@Mod(value = "example_mod", name = "Example Mod", version = "1.0.0", description = "这是一个示例模组，演示Flint模组开发")
public class ExampleMod implements IFlintMod {
    
    @Override
    public void onInitialize() {
        logger.info("Example Mod is initializing...");
        
        Mod modAnnotation = this.getClass().getAnnotation(Mod.class);
        logger.info("Mod ID: " + modAnnotation.value());
        logger.info("Example Mod initialized successfully!");
        
        registerModContent();
    }
    
    @Override
    public void onPreStartup() {
        logger.info("Example Mod is executing pre-startup logic...");
    }
    
    @Override
    public void onStartup() {
        logger.info("Example Mod is executing startup logic...");
    }
    
    @Override
    public void onPostStartup() {
        logger.info("Example Mod is executing post-startup logic...");
    }
    
    @Override
    public void onInGame() {
        // 在游戏中定期执行的逻辑
        // 由于此方法会频繁调用，可以在此处实现游戏中的动态逻辑
    }
    
    @Override
    public void onPreShutdown() {
        logger.info("Example Mod is executing pre-shutdown logic...");
    }
    
    @Override
    public void onShutdown() {
        logger.info("Example Mod is executing shutdown logic...");
    }
    
    private void registerModContent() {
        logger.info("Registering mod content...");
        logger.info("Mod content registered successfully!");
    }
}