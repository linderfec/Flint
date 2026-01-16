package net.Flint.mixin;

import net.Flint.logs.logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

/**
 * Mixin初始化器
 * 用于初始化和配置Mixin系统
 */
public class MixinInitializer {

    /**
     * 初始化Mixin系统
     */
    public static void initialize() {
        try {
            logger.info("Initializing Mixin System...");
            
            // 启动Mixin引导程序
            MixinBootstrap.init();
            
            // 添加Mixin配置
            Mixins.addConfiguration("flint.mixins.json");
            
            logger.info("Mixin System initialized successfully!");
        } catch (Exception e) {
            logger.error("Failed to initialize Mixin System: " + e.getMessage());
            e.printStackTrace();
        }
    }
}