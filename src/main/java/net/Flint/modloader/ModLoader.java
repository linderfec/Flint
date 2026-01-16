package net.Flint.modloader;

import net.Flint.api.FlintAPI;
import net.Flint.lifecycle.LifecycleManager;
import net.Flint.logs.logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 模组加载器类 - 用于加载模组JAR文件的自定义类加载器
 * 继承URLClassLoader，提供对模组文件的特殊加载逻辑和生命周期管理
 */
public class ModLoader extends URLClassLoader {
    private final String modId;

    /**
     * 构造函数 - 创建模组加载器实例
     * 
     * @param modJarFile 模组JAR文件
     * @param modId 模组ID
     * @throws IOException 当文件操作出现异常时抛出
     */
    public ModLoader(File modJarFile, String modId) throws IOException {
        super(new URL[] {modJarFile.toURI().toURL()}, ModLoader.class.getClassLoader());
        this.modId = modId;
    }

    /**
     * 构造函数 - 创建模组加载器实例
     * 
     * @param modJarFile 模组JAR文件
     * @throws IOException 当文件操作出现异常时抛出
     */
    public ModLoader(File modJarFile) throws IOException {
        super(new URL[] {modJarFile.toURI().toURL()}, ModLoader.class.getClassLoader());
        this.modId = "unknown";  // 默认模组ID
    }

    /**
     * 重写类加载方法 - 优先从当前加载器中查找类，如果找不到则委托给父加载器
     * 
     * @param name 要加载的类名
     * @return 加载的类
     * @throws ClassNotFoundException 当类无法找到时抛出
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            // 首先尝试从当前加载器查找类
            return findClass(name);
        } catch (ClassNotFoundException e) {
            // 如果找不到，则委托给父加载器
            return super.loadClass(name);
        }
    }

    /**
     * 获取模组ID
     * 
     * @return 模组ID
     */
    public String getModId() {
        return modId;
    }

    /**
     * 注册模组到生命周期管理器
     * 
     * @param modInstance 模组实例
     */
    public void registerModToLifecycle(Object modInstance) {
        if (modInstance instanceof net.Flint.lifecycle.LifecycleEvent) {
            LifecycleManager.registerModLifecycle(this.modId, (net.Flint.lifecycle.LifecycleEvent) modInstance);
            logger.info("Registered mod " + this.modId + " to lifecycle manager");
        } else {
            logger.warn("Mod " + this.modId + " does not implement LifecycleEvent interface");
        }
    }
    
    /**
     * 注册模组实例到API系统
     * 
     * @param modInstance 模组实例
     */
    public void registerModToAPI(Object modInstance) {
        FlintAPI.registerModInstance(this.modId, modInstance);
        logger.info("Registered mod " + this.modId + " to API system");
    }
}
