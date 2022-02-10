package app.socketiot.server.launcher;

import java.net.BindException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.cli.ArgParser;
import app.socketiot.server.servers.BaseServer;
import app.socketiot.server.servers.HttpApiServer;
import app.socketiot.server.workers.CertificateWorker;
import app.socketiot.server.workers.DBWorker;;

public class Launcher {
    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);

        Holder holder = new Holder(argParser);

        BaseServer[] servers = new BaseServer[] {
                new HttpApiServer(holder),
        };

        if (startServers(servers)) {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new DBWorker(holder), 6000, 6000, TimeUnit.MILLISECONDS);
            scheduler.scheduleAtFixedRate(new CertificateWorker(holder.sslprovider), 1, 1, TimeUnit.DAYS);
            Runtime.getRuntime().addShutdownHook(new Thread(new ExitLauncher(servers, holder, scheduler)));
            System.out.println("Server Started");
            holder.sslprovider.generateInitialCertificates(holder.props);
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
