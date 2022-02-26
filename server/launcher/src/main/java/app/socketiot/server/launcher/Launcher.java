package app.socketiot.server.launcher;

import java.net.BindException;
import java.security.Security;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.cli.ArgParser;
import app.socketiot.server.core.cli.properties.ServerProperties;
import app.socketiot.server.servers.BaseServer;
import app.socketiot.server.servers.HttpApiServer;
import app.socketiot.server.utils.LoggerUtil;
import app.socketiot.server.workers.CertificateWorker;
import app.socketiot.server.workers.DBWorker;

public class Launcher {

    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);

        Security.addProvider(new BouncyCastleProvider());

        String logsFolder = argParser.getArg("-logsFolder");

        System.setProperty("logsFolder", logsFolder == null ? "./logs" : logsFolder);

        ServerProperties props = new ServerProperties();

        String logLevel = props.getProperty("server.log.level", "info");
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("AsyncLogger.RingBufferSize",
                props.getProperty("async.logger.ring.buffer.size", "2048"));
        LoggerUtil.changeLogLevel(logLevel);

        Holder holder = new Holder(argParser, props);

        BaseServer[] servers = new BaseServer[] {
                new HttpApiServer(holder),
        };

        if (startServers(servers)) {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new DBWorker(holder), 6000, 6000, TimeUnit.MILLISECONDS);
            scheduler.scheduleAtFixedRate(new CertificateWorker(holder.sslprovider), 1, 1, TimeUnit.DAYS);
            Runtime.getRuntime().addShutdownHook(new Thread(new ExitLauncher(servers, holder, scheduler)));
            holder.sslprovider.generateInitialCertificates(holder.props);
            System.out.println("Server Started");
        }
    }

    public static boolean startServers(BaseServer[] servers) {
        try {
            for (BaseServer server : servers) {
                server.start();
            }
            return true;
        } catch (BindException e) {
            System.err.println("Port Already in use");
        } catch (Exception e) {
            System.err.println("Error starting server");
        }
        return false;
    }

}
