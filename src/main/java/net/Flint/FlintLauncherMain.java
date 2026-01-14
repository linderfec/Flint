package net.Flint;

import net.Flint.modloader.FlintMod;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlintLauncherMain {
    public static final int ASM_VERSION = Opcodes.ASM9;
    public static final String FLINTVERSION = "1.21.10-1.0";
    public static final String MODLUNACHER_ID = "flint";
    public static final String DEFAULT_MODS_DIR = "mod";
    public static final String DEFAULT_SYSMODLIB_DIR = "sysmodlib";
    public static final String DEFAULT_CACHE_DIR = ".cache";
    public static final String DEFAULT_CONFIG_DIR = "config";
    public static final String CACHE_DIR_NAME = "flint";
    private static final String PROCESSED_MODS_DIR_NAME = "processedMods";
    public static final String REMAPPED_JARS_DIR_NAME = "remappedJars";
    private static final String TMP_DIR_NAME = "tmp";

    public static void main(String[] args) {
        String mcJarPath = null;
        String gameDir = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--mcjar") && i + 1 < args.length) {
                mcJarPath = args[i + 1];
            } else if (args[i].equals("--gameDir") && i + 1 < args.length) {
                gameDir = args[i + 1];
            }
        }

        FlintMod flintMod = new FlintMod();
        flintMod.start();

        try {
            ClassLoader currentClassLoader = FlintLauncherMain.class.getClassLoader();
            List<URL> urls = new ArrayList<>();
            
            if (currentClassLoader instanceof URLClassLoader) {
                urls.addAll(Arrays.asList(((URLClassLoader) currentClassLoader).getURLs()));
            }
            
            if (mcJarPath != null) {
                urls.add(new File(mcJarPath).toURI().toURL());
            }
            
            if (gameDir != null) {
                File libsDir = new File(gameDir, "libraries");
                if (libsDir.exists() && libsDir.isDirectory()) {
                    addJarsFromDirectory(libsDir, urls);
                }
            }

            URLClassLoader newClassLoader = new URLClassLoader(urls.toArray(new URL[0]), currentClassLoader.getParent());
            Thread.currentThread().setContextClassLoader(newClassLoader);
            
            Class<?> mcMainClass = newClassLoader.loadClass("net.minecraft.client.main.Main");
            Method mainMethod = mcMainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void addJarsFromDirectory(File directory, List<URL> urls) throws Exception {
        File[] jarFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                if (jarFile.isFile()) {
                    urls.add(jarFile.toURI().toURL());
                }
            }
        }
        
        File[] subDirs = directory.listFiles(File::isDirectory);
        if (subDirs != null) {
            for (File subDir : subDirs) {
                addJarsFromDirectory(subDir, urls);
            }
        }
    }
    
    private static void addUrlToAppClassLoader(URL url) {
        System.err.println("Warning: Cannot dynamically add JAR to classpath in Java 9+. " +
                          "Please start with --add-opens jdk.internal.loader to access internal classes, " +
                          "or use a custom classloader. Falling back to default behavior.");
    }
}
