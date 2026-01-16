package net.Flint.lifecycle;

/**
 * 生命周期阶段枚举 - 定义模组可以响应的不同生命周期阶段
 */
public enum LifecyclePhase {
    /**
     * 模组初始化阶段 - 在模组加载时触发
     */
    INITIALIZATION,
    
    /**
     * 游戏启动前阶段 - 在游戏主循环开始前触发
     */
    PRE_STARTUP,
    
    /**
     * 游戏启动阶段 - 在游戏主循环开始时触发
     */
    STARTUP,
    
    /**
     * 游戏启动后阶段 - 在游戏主循环开始后触发
     */
    POST_STARTUP,
    
    /**
     * 游戏运行中阶段 - 在游戏运行过程中定期触发
     */
    IN_GAME,
    
    /**
     * 游戏关闭前阶段 - 在游戏关闭前触发
     */
    PRE_SHUTDOWN,
    
    /**
     * 游戏关闭阶段 - 在游戏关闭时触发
     */
    SHUTDOWN
}