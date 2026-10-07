package org.flint.modsrt;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModsrtLoader {

    private final Path modsDir;
    private final Instrumentation inst;
    private final List<Modsrt> mods = new ArrayList<>();

    public ModsrtLoader(String modsDir, Instrumentation inst) {
        this.modsDir = Paths.get(modsDir);
        this.inst = inst;
    }

    public void loadAll() {
        if (!Files.exists(modsDir)) {
            System.out.println("[Modsrt] 目录不存在: " + modsDir);
            return;
        }

        System.out.println("[Modsrt] 扫描目录: " + modsDir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsDir, "*.jar")) {
            for (Path jarPath : stream) {
                loadJar(jarPath);
            }
        } catch (Exception e) {
            System.err.println("[Modsrt] 扫描失败: " + e.getMessage());
        }
    }

    private void loadJar(Path jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath.toFile());
            inst.appendToSystemClassLoaderSearch(jarFile);

            try (URLClassLoader loader = new URLClassLoader(
                    new URL[]{jarPath.toUri().toURL()},
                    ModsrtLoader.class.getClassLoader())) {

                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) continue;

                    String className = entry.getName()
                            .replace('/', '.')
                            .replace(".class", "");

                    try {
                        Class<?> clazz = loader.loadClass(className);
                        MODS annotation = clazz.getAnnotation(MODS.class);
                        if (annotation == null) continue;

                        if (!Modsrt.class.isAssignableFrom(clazz)) {
                            System.err.println("[Modsrt] @" + annotation.value()
                                    + " 类未实现 Modsrt 接口: " + className);
                            continue;
                        }

                        Modsrt mod = (Modsrt) clazz.getDeclaredConstructor().newInstance();
                        mod.onLoad();
                        mods.add(mod);
                        System.out.println("[Modsrt] 已加载: " + annotation.value()
                                + " -> " + className);

                    } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("[Modsrt] JAR 加载失败: " + jarPath.getFileName()
                    + " - " + e.getMessage());
        }
    }

    public List<Modsrt> getMods() {
        return Collections.unmodifiableList(mods);
    }

    public void shutdownAll() {
        for (Modsrt m : mods) {
            m.onUnload();
        }
        mods.clear();
        System.out.println("[Modsrt] 所有 Mod 已卸载");
    }
}
