package net.Flint;

import org.objectweb.asm.Opcodes;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

public class FlintLauncherMain {
    //ASM9 版本 用于字节的控制
    public static final int ASM_VERSION = Opcodes.ASM9;

    //版本号，加载器的id，模组的目录名，处理系统的目录名，配置目录名,缓存目录
    public static final String FLINTVERSION = "1.21.10-beta-1.0";
    public static final String MODLUNACHER_ID = "flint";
    public static final String DEFAULT_MODS_DIR = "mod";
    public static final String DEFAULT_SYSMODLIB_DIR = "sysmodlib";
    public static final String DEFAULT_CACHE_DIR = ".cache";
    public static final String DEFAULT_CONFIG_DIR = "config";

    //缓存目录名，用于存放 FLint 加载器相关缓存
    public static final String CACHE_DIR_NAME = "flint";
    //处理过的模组目录名，相对于加载器缓存目录
    private static final String PROCESSED_MODS_DIR_NAME = "processedMods";
    //重映射 JAR 文件目录名，相对于加载器缓存目录
    public static final String REMAPPED_JARS_DIR_NAME = "remappedJars";
    //临时目录名，相对于加载器缓存目录
    private static final String TMP_DIR_NAME = "tmp";

    public static void main(String[] args) {
        // 解析参数中的Minecraft JAR路径
        String mcJarPath = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--mcjar") && i + 1 < args.length) {
                mcJarPath = args[i + 1];
                break;
            }
        }

        if (mcJarPath != null) {
            try {
                // 添加Minecraft JAR到类路径
                File mcJarFile = new File(mcJarPath);
                URL mcJarUrl = mcJarFile.toURI().toURL();
                
                // 使用系统类加载器添加JAR（Java 9+兼容性处理）
                ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
                if (sysClassLoader instanceof URLClassLoader) {
                    URLClassLoader urlClassLoader = (URLClassLoader) sysClassLoader;
                    Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    method.invoke(urlClassLoader, mcJarUrl);
                } else {
                    // 对于Java 9+，使用反射或其他机制
                }
                
                Class<?> mcMainClass = Class.forName("net.minecraft.client.main.Main");
                java.lang.reflect.Method mainMethod = mcMainClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Class<?> mcMainClass = Class.forName("net.minecraft.client.main.Main");
                java.lang.reflect.Method mainMethod = mcMainClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
