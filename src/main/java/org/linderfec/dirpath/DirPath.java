package org.linderfec.dirpath;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DirPath {

    private static void log(String msg) {
        String home = System.getProperty("user.home", ".");
        String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (PrintWriter pw = new PrintWriter(new FileWriter(home + "/flint-dirpath.log", true))) {
            pw.println("[" + ts + "] " + msg);
        } catch (IOException e) {
            System.err.println("[Flint-DirPath] 写日志失败: " + e.getMessage());
        }
    }
    // 日志目录
    private static String loggerDir = "logs";
    // 各种系统的目录
    private static String pubSystem = "pubSystem";
    // 模组目录文件
    private static String mods = "mods";
    // 包管理的txt文件
    private static String packager = "packager.txt";

    /**
     * 这个是一个内置的文件系统
     * 用来确定所有需要的文件的文件目录
     *
     * dirPath 指定的完整的游戏目录
     */
    public static void dirpath(String[] arge) {
        if (arge == null || arge.length == 0) return;
        String dirPath = null;
        for (String arg : arge) {
            if (arg.startsWith("--gamedir=")) {
                dirPath = arg.substring("--gamedir=".length());
                break;
            }
        }
        if (dirPath == null) dirPath = arge[0];

        log("游戏目录: " + dirPath);
        loggerDir = Paths.get(dirPath, "logs").toString();
        pubSystem = Paths.get(dirPath,"pubSystem").toString();
        mods = Paths.get(dirPath, "mods").toString();
        packager = Paths.get(dirPath, "packager.txt").toString();
        log("packager路径: " + packager);

        // 创建 logs 目录
        FileUtil.createDirIfNotExist(loggerDir);
        // 创建 pubSystem 目录
        FileUtil.createDirIfNotExist(pubSystem);
        // 创建 mods 目录
        FileUtil.createDirIfNotExist(mods);
        // 创建 packager.txt 包管理文件
        FileUtil.createFileIfNotExist(packager);

    }

    public static String loggerdir() {
        return loggerDir;
    }
    public static String pubsystem() {
        return pubSystem;
    }
    public static String modstader() {
        return mods;
    }

}
