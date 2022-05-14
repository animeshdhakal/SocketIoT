package app.socketiot.server.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerUtil {
    public static void changeLogLevel(String level) {
        Level newLevel = Level.valueOf(level);
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), newLevel);
    }

    public static void init(String logLevel, String bufferSize) {
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("AsyncLogger.RingBufferSize", bufferSize);
        changeLogLevel(logLevel);
    }

}
