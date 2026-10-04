package org.linderfec.dirpath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    private static final String LOG_FILE;
    static {
        String home = System.getProperty("user.home", ".");
        LOG_FILE = home + "/flint-fileutil.log";
        log("FileUtil initialized, log file: " + LOG_FILE);
    }

    private static void log(String msg) {
        String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String line = "[" + ts + "] " + msg;
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.println(line);
        } catch (IOException e) {
            System.err.println("[Flint] 写日志失败: " + e.getMessage());
        }
    }

    /**
     * 创建目录（如果不存在）
     * @param dirPath 目录路径
     * @return true=成功或已存在，false=创建失败
     */
    public static boolean createDirIfNotExist(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            return false;
        }

        File dir = new File(dirPath);

        if (dir.exists()) {
            if (dir.isDirectory()) {
                log("目录已存在: " + dirPath);
                return true;
            } else {
                log("路径已存在但不是目录: " + dirPath);
                return false;
            }
        }

        boolean created = dir.mkdirs();
        if (created) {
            log("目录创建成功: " + dirPath);
        } else {
            log("目录创建失败: " + dirPath);
        }
        return created;
    }

    public static boolean createFileIfNotExist(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        File file = new File(filePath);

        if (file.exists()) {
            return file.isFile();
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try {
            return file.createNewFile();
        } catch (IOException e) {
            log("文件创建失败: " + filePath + " - " + e.getMessage());
            return false;
        }
    }
}