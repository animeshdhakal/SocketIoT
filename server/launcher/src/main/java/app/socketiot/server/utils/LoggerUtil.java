package app.socketiot.server.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import app.socketiot.server.cli.ArgParser;
import app.socketiot.server.cli.properties.ServerProperties;

public class LoggerUtil {
    public static void configureLogger(ArgParser argParser, ServerProperties props) {
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("AsyncLogger.RingBufferSize",
                props.getProperty("async.logger.ring.buffer.size", "2048"));

        String df = argParser.getArg("-df");

        if (df == null) {
            df = "SocketIoT";
        }

        System.setProperty("df", df);

        changeLogLevel(props.getProperty("log.level", "info"));
    }

    public static void changeLogLevel(String level) {
        Level lev = Level.valueOf(level);
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), lev);
    }
}
