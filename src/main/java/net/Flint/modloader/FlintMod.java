package net.Flint.modloader;

import net.Flint.lifecycle.LifecycleEvent;
import net.Flint.lifecycle.LifecycleManager;
import net.Flint.lifecycle.LifecyclePhase;
import net.Flint.logs.logger;

/**
 * Flint模组基础类 - 模组系统的基础实现
 * 实现LifecycleEvent接口，提供完整的生命周期管理功能
 */
public class FlintMod implements LifecycleEvent {
    
    /**
     * 启动Flint模组系统
     * 初始化模组系统并触发初始化生命周期事件
     */
    public void start() {
        logger.info("Flint Mod is starting...");
        
        // 触发初始化生命周期阶段
        LifecycleManager.triggerLifecyclePhase(LifecyclePhase.INITIALIZATION);
    }

    @Override
    public void onInitialize() {
        logger.info("FlintMod: Executing initialization phase");
        // 执行初始化逻辑
    }

    @Override
    public void onPreStartup() {
        logger.info("FlintMod: Executing pre-startup phase");
        // 执行预启动逻辑
    }

    @Override
    public void onStartup() {
        logger.info("FlintMod: Executing startup phase");
        // 执行启动逻辑
    }

    @Override
    public void onPostStartup() {
        logger.info("FlintMod: Executing post-startup phase");
        // 执行启动后逻辑
    }

    @Override
    public void onInGame() {
        // 游戏中定期执行的逻辑（如果需要）
    }

    @Override
    public void onPreShutdown() {
        logger.info("FlintMod: Executing pre-shutdown phase");
        // 执行预关闭逻辑
    }

    @Override
    public void onShutdown() {
        logger.info("FlintMod: Executing shutdown phase");
        // 执行关闭逻辑
    }
}
