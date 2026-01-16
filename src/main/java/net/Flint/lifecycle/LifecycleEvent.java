package net.Flint.lifecycle;

/**
 * 生命周期事件接口 - 定义模组可以实现的生命周期事件回调
 */
public interface LifecycleEvent {
    
    /**
     * 模组初始化时调用
     */
    default void onInitialize() {
        // 默认空实现，子类可选择性实现
    }
    
    /**
     * 游戏启动前调用
     */
    default void onPreStartup() {
        // 默认空实现，子类可选择性实现
    }
    
    /**
     * 游戏启动时调用
     */
    default void onStartup() {
        // 默认空实现，子类可选择性实现
    }
    
    /**
     * 游戏启动后调用
     */
    default void onPostStartup() {
        // 默认空实现，子类可选择性实现
    }
    
    /**
     * 游戏运行中定期调用
     */
    default void onInGame() {
        // 默认空实现，子类可选择性实现
    }
    
    /**
     * 游戏关闭前调用
     */
    default void onPreShutdown() {
        // 默认空实现，子类可选择性实现
    }
    
    /**
     * 游戏关闭时调用
     */
    default void onShutdown() {
        // 默认空实现，子类可选择性实现
    }
}