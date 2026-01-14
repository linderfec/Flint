package net.Flint.modloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static net.Flint.logs.logger.logger;

public class FlintMod {
    //模组存放目录
    private static final File MODS_DIR = new File("mods");
    //系统依赖存放目录
    private static final File SYSMODLIB_DIR = new File("mods/sysmodlib");

    //加载的模组列表
    private final List<Object> loadedMods = new ArrayList<>();

    public void start() {
        if (!MODS_DIR.exists() && !MODS_DIR.mkdir()) {
            logger.info("Unable to create a mod catalog:" + MODS_DIR.getAbsolutePath());
        }

        File[] modJars = MODS_DIR.listFiles((dir, name) -> name.endsWith(".jar"));
        if (modJars == null || modJars.length == 0) {
            logger.info("No mods installed");
            return;
        }

        for (File modJar : modJars) {
            loadMod(modJar);
        }
        
        initMods();
    }

    private void loadMod(File modJar) {
        try {
            ModInfo modInfo = ModConfigParser.parseModInfoFromJar(modJar);
            
            if (modInfo.getMainClass() == null || modInfo.getMainClass().isEmpty()) {
                logger.info("Mod " + modJar.getName() + " does not have a specified main class in its configuration!");
                return;
            }
            
            ModLoader modLoader = new ModLoader(modJar);
            String modMainClassName = modInfo.getMainClass();
            Class<?> modClass = modLoader.loadClass(modMainClassName);
            
            if (modClass.isAnnotationPresent(Mod.class) && IFlintMod.class.isAssignableFrom(modClass)) {
                Object modInstance = modClass.getDeclaredConstructor().newInstance();
                loadedMods.add(modInstance);
                
                Mod modAnnotation = modClass.getAnnotation(Mod.class);
                String modId = modAnnotation.value();
                
                logger.info("Mod " + modJar.getName() + " (ID: " + modId + ") loaded successfully.");
            } else {
                logger.info("Mod " + modJar.getName() + " is not a valid Flint mod!");
            }
        } catch (Exception e) {
            logger.info("Error loading mod " + modJar.getName() + ": ");
            e.printStackTrace();
        }
    }

    private void initMods() {
        for (Object mod : loadedMods) {
            if (mod instanceof IFlintMod) {
                try {
                    ((IFlintMod) mod).onInitialize();
                    
                    Class<?> modClass = mod.getClass();
                    if (modClass.isAnnotationPresent(Mod.class)) {
                        Mod modAnnotation = modClass.getAnnotation(Mod.class);
                        logger.info("Mod " + modAnnotation.value() + " initialized");
                    } else {
                        logger.info("Mod " + modClass.getSimpleName() + " initialized");
                    }
                } catch (Exception e) {
                    logger.info("Failed to initialize mod " + mod.getClass().getSimpleName());
                    e.printStackTrace();
                }
            }
        }
    }
}
