package net.Flint.example;

import net.Flint.modloader.Mod;
import net.Flint.modloader.IFlintMod;
import static net.Flint.logs.logger.logger;

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
    
    private void registerModContent() {
        logger.info("Registering mod content...");
        logger.info("Mod content registered successfully!");
    }
}