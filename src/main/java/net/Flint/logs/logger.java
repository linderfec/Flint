package net.Flint.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class logger {
    public static final Logger logger = LoggerFactory.getLogger(logger.class);

    public static void setLogger(String message) {
        logger.info(message);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }
}