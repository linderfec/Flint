package net.Flint;

import net.Flint.api.FlintAPI;
import net.Flint.lifecycle.InGameLifecycleManager;
import net.Flint.lifecycle.LifecycleManager;
import net.Flint.lifecycle.LifecyclePhase;
import net.Flint.modloader.FlintMod;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Flint启动器主类 - 启动Minecraft客户端的主入口点
 * 负责解析启动参数、初始化模组加载器、管理模组生命周期和API系统，加载Minecraft并启动游戏
 */
public class FlintLauncherMain {
    /**
     * ASM字节码操作框架版本常量
     */
    public static final int ASM_VERSION = Opcodes.ASM9;
    
    /**
     * Flint模组加载器版本号
     */
    public static final String FLINTVERSION = "1.21.10-1.0";
    
    /**
     * 模组加载器ID标识符
     */
    public static final String MODLUNACHER_ID = "flint";
    
    /**
     * 默认模组目录路径
     */
    public static final String DEFAULT_MODS_DIR = "mod";
    
    /**
     * 默认系统模组库目录路径
     */
    public static final String DEFAULT_SYSMODLIB_DIR = "sysmodlib";
    
    /**
     * 默认缓存目录路径
     */
    public static final String DEFAULT_CACHE_DIR = ".cache";
    
    /**
     * 默认配置目录路径
     */
    public static final String DEFAULT_CONFIG_DIR = "config";
    
    /**
     * 缓存目录名称
     */
    public static final String CACHE_DIR_NAME = "flint";
    
    /**
     * 已处理模组目录名称
     */
    private static final String PROCESSED_MODS_DIR_NAME = "processedMods";
    
    /**
     * 重映射JAR文件目录名称
     */
    public static final String REMAPPED_JARS_DIR_NAME = "remappedJars";
    
    /**
     * 临时目录名称
     */
    private static final String TMP_DIR_NAME = "tmp";

    /**
     * 程序主入口点
     * 解析启动参数，初始化模组加载器，管理模组生命周期和API系统，加载Minecraft并启动游戏
     * 
     * @param args 启动参数数组
     *             支持 "--mcjar" 参数指定Minecraft JAR文件路径
     *             支持 "--gameDir" 参数指定游戏目录路径
     */
    public static void main(String[] args) {
        String mcJarPath = null;
        String gameDir = null;
        
        // 解析启动参数
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--mcjar") && i + 1 < args.length) {
                mcJarPath = args[i + 1];
            } else if (args[i].equals("--gameDir") && i + 1 < args.length) {
                gameDir = args[i + 1];
            }
        }

        // 初始化Flint API系统
        FlintAPI.initialize();
        
        // 启动Flint模组并触发初始化生命周期
        FlintMod flintMod = new FlintMod();
        flintMod.start();

        try {
            // 触发预启动生命周期
            LifecycleManager.triggerLifecyclePhase(LifecyclePhase.PRE_STARTUP);

            // 获取当前类加载器
            ClassLoader currentClassLoader = FlintLauncherMain.class.getClassLoader();
            List<URL> urls = new ArrayList<>();
            
            // 添加当前类加载器中的URL
            if (currentClassLoader instanceof URLClassLoader) {
                urls.addAll(Arrays.asList(((URLClassLoader) currentClassLoader).getURLs()));
            }
            
            // 添加Minecraft JAR文件路径
            if (mcJarPath != null) {
                urls.add(new File(mcJarPath).toURI().toURL());
            }
            
            // 添加游戏目录中的库文件
            if (gameDir != null) {
                File libsDir = new File(gameDir, "libraries");
                if (libsDir.exists() && libsDir.isDirectory()) {
                    addJarsFromDirectory(libsDir, urls);
                }
            }

            // 创建新的URLClassLoader
            URLClassLoader newClassLoader = new URLClassLoader(urls.toArray(new URL[0]), currentClassLoader.getParent());
            Thread.currentThread().setContextClassLoader(newClassLoader);
            
            // 触发启动生命周期
            LifecycleManager.triggerLifecyclePhase(LifecyclePhase.STARTUP);
            
            // 启动游戏内生命周期管理器
            InGameLifecycleManager.start();
            
            // 加载并启动Minecraft主类
            Class<?> mcMainClass = newClassLoader.loadClass("net.minecraft.client.main.Main");
            Method mainMethod = mcMainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
            
            // 触发启动后生命周期
            LifecycleManager.triggerLifecyclePhase(LifecyclePhase.POST_STARTUP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 添加关闭钩子以处理关闭生命周期和API清理
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 停止游戏内生命周期管理器
            InGameLifecycleManager.stop();
            
            LifecycleManager.triggerLifecyclePhase(LifecyclePhase.PRE_SHUTDOWN);
            LifecycleManager.triggerLifecyclePhase(LifecyclePhase.SHUTDOWN);
            
            // 清理API系统
            FlintAPI.cleanup();
        }));
    }
    
    /**
     * 递归添加目录中的JAR文件到URL列表
     * 
     * @param directory 要搜索的目录
     * @param urls 存储JAR文件URL的列表
     * @throws Exception 当文件操作出现异常时抛出
     */
    private static void addJarsFromDirectory(File directory, List<URL> urls) throws Exception {
        // 获取目录中的JAR文件
        File[] jarFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                if (jarFile.isFile()) {
                    urls.add(jarFile.toURI().toURL());
                }
            }
        }
        
        // 递归处理子目录
        File[] subDirs = directory.listFiles(File::isDirectory);
        if (subDirs != null) {
            for (File subDir : subDirs) {
                addJarsFromDirectory(subDir, urls);
            }
        }
    }
    
    /**
     * 向应用类加载器添加URL的警告方法
     * 
     * 在Java 9+版本中，由于模块系统的限制，无法动态向应用类加载器添加JAR文件
     * 
     * @param url 要添加的URL
     */
    @SuppressWarnings("unused")
    private static void addUrlToAppClassLoader(URL url) {
        System.err.println("Warning: Cannot dynamically add JAR to classpath in Java 9+. " +
                          "Please start with --add-opens jdk.internal.loader to access internal classes, " +
                          "or use a custom classloader. Falling back to default behavior.");
    }
}
