package net.Flint.lifecycle;

import net.Flint.logs.logger;

/**
 * 游戏内生命周期管理器 - 负责在游戏运行过程中定期触发IN_GAME生命周期事件
 * 这个管理器运行在一个独立的线程中，以确保IN_GAME事件能定期触发
 */
public class InGameLifecycleManager {
    /**
     * 是否正在运行
     */
    private static volatile boolean running = false;
    
    /**
     * 游戏内事件调度线程
     */
    private static Thread inGameThread;
    
    /**
     * 触发间隔（毫秒）
     */
    private static final long TRIGGER_INTERVAL = 50; // 每50毫秒触发一次，约20次/秒
    
    /**
     * 启动游戏内生命周期管理器
     */
    public static synchronized void start() {
        if (running) {
            logger.warn("InGameLifecycleManager is already running");
            return;
        }
        
        running = true;
        inGameThread = new Thread(InGameLifecycleManager::runLoop, "Flint-InGame-Lifecycle");
        inGameThread.setDaemon(true); // 设置为守护线程
        inGameThread.start();
        
        logger.info("InGameLifecycleManager started");
    }
    
    /**
     * 停止游戏内生命周期管理器
     */
    public static synchronized void stop() {
        if (!running) {
            logger.warn("InGameLifecycleManager is not running");
            return;
        }
        
        running = false;
        if (inGameThread != null) {
            try {
                inGameThread.join(2000); // 最多等待2秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for InGameLifecycleManager to stop");
            }
        }
        
        logger.info("InGameLifecycleManager stopped");
    }
    
    /**
     * 运行循环
     */
    private static void runLoop() {
        logger.info("InGameLifecycleManager loop started");
        
        while (running) {
            try {
                // 触发IN_GAME生命周期阶段
                LifecycleManager.triggerInGamePhase();
                
                // 等待指定间隔
                Thread.sleep(TRIGGER_INTERVAL);
            } catch (InterruptedException e) {
                // 线程被中断，正常退出
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in InGameLifecycleManager loop: " + e.getMessage());
                // 继续运行，不要因为单个错误停止整个循环
                try {
                    Thread.sleep(100); // 出错时稍作延迟再继续
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        logger.info("InGameLifecycleManager loop ended");
    }
    
    /**
     * 检查管理器是否正在运行
     * 
     * @return 如果正在运行返回true，否则返回false
     */
    public static boolean isRunning() {
        return running;
    }
}