package net.Flint.api;

import net.Flint.lifecycle.LifecycleManager;
import net.Flint.lifecycle.ModManager;
import net.Flint.modloader.ModInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flint API主类 - 提供对Flint模组系统核心功能的统一访问接口
 * 这是模组开发者与Flint系统交互的主要入口点
 */
public class FlintAPI {
    /**
     * 存储模组ID到模组实例的映射
     */
    private static final Map<String, Object> modInstances = new ConcurrentHashMap<>();
    
    /**
     * 获取Flint版本信息
     * 
     * @return Flint版本字符串
     */
    public static String getVersion() {
        return net.Flint.FlintLauncherMain.FLINTVERSION;
    }
    
    /**
     * 检查API是否已初始化
     * 
     * @return 如果API已准备好返回true，否则返回false
     */
    public static boolean isInitialized() {
        return true;
    }
    
    /**
     * 初始化Flint API系统
     * 这个方法应该在模组系统启动时调用
     */
    public static void initialize() {
        System.out.println("Flint API initialized. Version: " + getVersion());
    }
    
    /**
     * 清理Flint API系统
     * 这个方法应该在模组系统关闭时调用
     */
    public static void cleanup() {
        // 清理模组管理器
        ModManager.shutdown();
        
        System.out.println("Flint API cleaned up.");
    }
    
    /**
     * 注册模组实例
     * 
     * @param modId 模组ID
     * @param modInstance 模组实例
     */
    public static void registerModInstance(String modId, Object modInstance) {
        modInstances.put(modId, modInstance);
    }
    
    /**
     * 获取已注册的模组数量
     * 
     * @return 已注册的模组数量
     */
    public static int getModCount() {
        return modInstances.size();
    }
    
    /**
     * 检查指定模组是否已注册
     * 
     * @param modId 模组ID
     * @return 如果模组已注册返回true，否则返回false
     */
    public static boolean isModRegistered(String modId) {
        return modInstances.containsKey(modId);
    }
    
    /**
     * 获取模组实例
     * 
     * @param modId 模组ID
     * @return 模组实例，如果不存在返回null
     */
    public static Object getModInstance(String modId) {
        return modInstances.get(modId);
    }
    
    /**
     * 获取所有已注册的模组ID
     * 
     * @return 模组ID集合
     */
    public static Set<String> getRegisteredModIds() {
        return LifecycleManager.getRegisteredModIds();
    }
    
    /**
     * 检查模组生命周期是否已注册
     * 
     * @param modId 模组ID
     * @return 如果模组已注册到生命周期系统返回true，否则返回false
     */
    public static boolean isModLifecycleRegistered(String modId) {
        return LifecycleManager.isModRegistered(modId);
    }
    
    /**
     * 获取所有模组信息
     * 
     * @return 模组信息列表
     */
    public static List<ModInfo> getAllModInfo() {
        return LifecycleManager.getAllModInfo();
    }
    
    /**
     * 动态加载模组
     * 
     * @param modPath 模组文件路径
     * @return CompletableFuture表示加载操作的结果
     */
    public static CompletableFuture<Boolean> loadModAsync(String modPath) {
        return ModManager.loadModAsync(modPath);
    }
    
    /**
     * 动态卸载模组
     * 
     * @param modId 要卸载的模组ID
     * @return CompletableFuture表示卸载操作的结果
     */
    public static CompletableFuture<Boolean> unloadModAsync(String modId) {
        return ModManager.unloadModAsync(modId);
    }
    
    /**
     * 动态重新加载模组
     * 
     * @param modId 要重新加载的模组ID
     * @return CompletableFuture表示重新加载操作的结果
     */
    public static CompletableFuture<Boolean> reloadModAsync(String modId) {
        return ModManager.reloadModAsync(modId);
    }
}