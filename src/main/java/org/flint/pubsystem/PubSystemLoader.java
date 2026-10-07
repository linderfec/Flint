package org.flint.pubsystem;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarFile;

/**
 * pubSystem 模块加载器
 * 扫描 run/pubSystem/ 目录，动态加载 jar 并初始化模块
 *
 * 用法：
 *   PubSystemLoader loader = new PubSystemLoader(pubSystemDir, inst);
 *   loader.loadAll();
 *   loader.getModule("logger");
 *   loader.shutdownAll();
 */
public class PubSystemLoader {

    private final Path moduleDir;
    private final Instrumentation inst;
    private final List<PubModule> modules = new ArrayList<>();

    public PubSystemLoader(String moduleDir, Instrumentation inst) {
        this.moduleDir = Paths.get(moduleDir);
        this.inst = inst;
    }

    /** 扫描目录并加载所有模块 jar */
    public void loadAll() {
        if (!Files.exists(moduleDir)) {
            System.out.println("[PubSystem] 目录不存在: " + moduleDir);
            return;
        }

        System.out.println("[PubSystem] 扫描模块目录: " + moduleDir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(moduleDir, "*.jar")) {
            for (Path jarPath : stream) {
                loadJar(jarPath);
            }
        } catch (Exception e) {
            System.err.println("[PubSystem] 扫描失败: " + e.getMessage());
        }
    }

    /** 加载单个 jar，通过 SPI 发现 PubModule 实现 */
    private void loadJar(Path jarPath) {
        try {
            JarFile jarFile = new JarFile(jarPath.toFile());
            inst.appendToSystemClassLoaderSearch(jarFile);

            try (URLClassLoader loader = new URLClassLoader(
                    new URL[]{jarPath.toUri().toURL()},
                    PubSystemLoader.class.getClassLoader())) {

                    ServiceLoader<PubModule> serviceLoader = ServiceLoader.load(PubModule.class, loader);
                for (PubModule module : serviceLoader) {
                    module.onLoad(inst);
                    modules.add(module);
                    System.out.println("[PubSystem] 已加载: " + module.getName() + " v" + module.getVersion());
                }
            }

        } catch (Exception e) {
            System.err.println("[PubSystem] 加载失败: " + jarPath.getFileName() + " - " + e.getMessage());
        }
    }

    /** 获取指定名称的模块 */
    public PubModule getModule(String name) {
        for (PubModule m : modules) {
            if (m.getName().equals(name)) return m;
        }
        return null;
    }

    /** 获取所有已加载的模块（不可修改） */
    public List<PubModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    /** 卸载指定名称的模块 */
    public boolean unloadModule(String name) {
        Iterator<PubModule> it = modules.iterator();
        while (it.hasNext()) {
            PubModule m = it.next();
            if (m.getName().equals(name)) {
                m.onUnload();
                it.remove();
                System.out.println("[PubSystem] 已卸载: " + name);
                return true;
            }
        }
        return false;
    }

    /** 卸载所有模块 */
    public void shutdownAll() {
        for (PubModule m : modules) {
            m.onUnload();
        }
        modules.clear();
        System.out.println("[PubSystem] 所有模块已卸载");
    }
}
