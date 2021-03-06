package com.ui.util;

import com.ui.entity.ComConstant;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

/**
 * 日志类
 * @author cz
 */
public class Log {
	private static Logger logger;

	static {
        logger = Logger.getLogger("sys_log");
        PropertyConfigurator.configure(new File(ComConstant.LOG_PATH).getAbsolutePath());
    }
	
	public static void logInfo(Object message) {
		logger.info("[INFO] " + message);
	}
	
	public static void logStep(Object message) {
		logger.info("[STEP] " + message);
	}
	
	public static void logFlow(Object message) {
		logger.info("[FLOW] " + message);
	}
	
	public static void logError(Object message, Throwable throwable) {
		logger.error("[ERROR]   " + message, throwable);
	}
}
