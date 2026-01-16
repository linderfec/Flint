package net.Flint.lifecycle;

import net.Flint.logs.logger;
import net.Flint.modloader.ModInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生命周期管理器 - 管理模组的生命周期事件
 * 负责在正确的时机触发模组的生命周期回调方法
 */
public class LifecycleManager {
    /**
     * 存储模组实例和其生命周期事件接口的映射
     */
    private static final Map<String, LifecycleEvent> modLifecycleMap = new ConcurrentHashMap<>();
    
    /**
     * 存储所有模组信息的列表
     */
    private static final List<ModInfo> modInfoList = new ArrayList<>();
    
    /**
     * 已触发的生命周期阶段集合，确保每个阶段只触发一次
     */
    private static final Set<LifecyclePhase> triggeredPhases = new HashSet<>();
    
    /**
     * 注册模组的生命周期事件
     * 
     * @param modId 模组ID
     * @param lifecycleEvent 模组的生命周期事件实现
     */
    public static void registerModLifecycle(String modId, LifecycleEvent lifecycleEvent) {
        modLifecycleMap.put(modId, lifecycleEvent);
        logger.info("Registered lifecycle for mod: " + modId);
    }
    
    /**
     * 注册模组信息
     * 
     * @param modInfo 模组信息对象
     */
    public static void registerModInfo(ModInfo modInfo) {
        modInfoList.add(modInfo);
        logger.info("Registered mod info: " + modInfo.getModId());
    }
    
    /**
     * 触发指定生命周期阶段的所有模组事件
     * 
     * @param phase 要触发的生命周期阶段
     */
    public static synchronized void triggerLifecyclePhase(LifecyclePhase phase) {
        if (phase == LifecyclePhase.IN_GAME) {
            // IN_GAME阶段特殊处理，不使用triggeredPhases集合
            // 因为IN_GAME阶段需要在游戏运行过程中多次触发
            triggerInGamePhase();
            return;
        }
        
        if (triggeredPhases.contains(phase)) {
            logger.debug("Lifecycle phase " + phase + " already triggered, skipping.");
            return;
        }
        
        logger.info("Triggering lifecycle phase: " + phase);
        
        // 标记此阶段已触发（除了IN_GAME阶段）
        triggeredPhases.add(phase);
        
        // 根据生命周期阶段调用相应的回调方法
        for (Map.Entry<String, LifecycleEvent> entry : modLifecycleMap.entrySet()) {
            String modId = entry.getKey();
            LifecycleEvent lifecycleEvent = entry.getValue();
            
            try {
                switch (phase) {
                    case INITIALIZATION:
                        lifecycleEvent.onInitialize();
                        break;
                    case PRE_STARTUP:
                        lifecycleEvent.onPreStartup();
                        break;
                    case STARTUP:
                        lifecycleEvent.onStartup();
                        break;
                    case POST_STARTUP:
                        lifecycleEvent.onPostStartup();
                        break;
                    case PRE_SHUTDOWN:
                        lifecycleEvent.onPreShutdown();
                        break;
                    case SHUTDOWN:
                        lifecycleEvent.onShutdown();
                        break;
                    default:
                        // IN_GAME阶段应该通过triggerInGamePhase()方法调用
                        break;
                }
                
                logger.debug("Executed lifecycle phase " + phase + " for mod: " + modId);
            } catch (Exception e) {
                logger.error("Error executing lifecycle phase " + phase + " for mod: " + modId + ", error: " + e.getMessage());
            }
        }
        
        logger.info("Completed lifecycle phase: " + phase);
    }
    
    /**
     * 触发游戏内生命周期阶段
     * 这个方法专门用于触发IN_GAME阶段，可以被InGameLifecycleManager定期调用
     */
    public static void triggerInGamePhase() {
        // 不检查triggeredPhases，因为IN_GAME阶段需要多次触发
        for (Map.Entry<String, LifecycleEvent> entry : modLifecycleMap.entrySet()) {
            String modId = entry.getKey();
            LifecycleEvent lifecycleEvent = entry.getValue();
            
            try {
                lifecycleEvent.onInGame();
                logger.debug("Executed IN_GAME phase for mod: " + modId);
            } catch (Exception e) {
                logger.error("Error executing IN_GAME phase for mod: " + modId + ", error: " + e.getMessage());
            }
        }
    }
    
    /**
     * 获取所有已注册的模组ID
     * 
     * @return 模组ID集合
     */
    public static Set<String> getRegisteredModIds() {
        return new HashSet<>(modLifecycleMap.keySet());
    }
    
    /**
     * 检查指定模组是否已注册
     * 
     * @param modId 模组ID
     * @return 如果模组已注册返回true，否则返回false
     */
    public static boolean isModRegistered(String modId) {
        return modLifecycleMap.containsKey(modId);
    }
    
    /**
     * 获取所有模组信息
     * 
     * @return 模组信息列表
     */
    public static List<ModInfo> getAllModInfo() {
        return new ArrayList<>(modInfoList);
    }
    
    /**
     * 重置生命周期管理器状态（用于测试或重启）
     */
    public static synchronized void reset() {
        modLifecycleMap.clear();
        modInfoList.clear();
        triggeredPhases.clear();
        logger.info("LifecycleManager has been reset.");
    }
    
    /**
     * 获取已触发的生命周期阶段集合
     * 
     * @return 已触发的阶段集合
     */
    public static Set<LifecyclePhase> getTriggeredPhases() {
        return new HashSet<>(triggeredPhases);
    }
    
    /**
     * 检查指定生命周期阶段是否已触发
     * 
     * @param phase 生命周期阶段
     * @return 如果已触发返回true，否则返回false
     */
    public static boolean hasPhaseTriggered(LifecyclePhase phase) {
        if (phase == LifecyclePhase.IN_GAME) {
            // IN_GAME阶段总是返回false，因为它需要多次触发
            return false;
        }
        return triggeredPhases.contains(phase);
    }
}