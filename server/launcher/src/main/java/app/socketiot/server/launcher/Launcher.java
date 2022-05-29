package app.socketiot.server.launcher;

import java.net.BindException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import app.socketiot.server.Holder;
import app.socketiot.server.cli.ArgParser;
import app.socketiot.server.cli.properties.ServerProperties;
import app.socketiot.server.servers.ServerBase;
import app.socketiot.server.servers.SocketIoTServer;
import app.socketiot.server.utils.LoggerUtil;
import app.socketiot.server.workers.DBWorker;

public class Launcher {

    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);

        ServerProperties props = new ServerProperties();

        LoggerUtil.configureLogger(argParser, props);

        Holder holder = new Holder(argParser, props);

        ServerBase[] servers = new ServerBase[] {
                new SocketIoTServer(holder)
        };

        for (ServerBase server : servers) {
            try {
                server.start();
            } catch (BindException exception) {
                System.err.println("Port Already In Use");
                return;
            } catch (Exception exception) {
                System.err.println("Error Starting Server");
                return;
            }
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new DBWorker(holder), holder.defaults.dbSaveInterval,
                holder.defaults.dbSaveInterval, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(new ExitLauncher(servers, holder, scheduler)));

        System.out.println("Server Started");
    }

}
