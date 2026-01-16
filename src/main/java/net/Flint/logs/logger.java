package net.Flint.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志记录器类 - 提供统一的日志记录接口
 * 封装了SLF4J日志框架的使用，简化日志记录操作
 */
public class logger {
    /**
     * SLF4J日志记录器实例
     */
    public static final Logger logger = LoggerFactory.getLogger(logger.class);

    public static void setLogger(String message) {
        logger.info(message);
    }

    public static void info(String message) {
        logger.info(message);
    }
    
    public static void info(String message, Throwable t) {
        logger.info(message, t);
    }

    public static void warn(String message) {
        logger.warn(message);
    }
    
    public static void warn(String message, Throwable t) {
        logger.warn(message, t);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void error(String message, Throwable t) {
        logger.error(message, t);
    }

    public static void debug(String message) {
        logger.debug(message);
    }
    
    public static void debug(String message, Throwable t) {
        logger.debug(message, t);
    }
}
