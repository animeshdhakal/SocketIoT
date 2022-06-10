package app.socketiot.server.launcher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.socketiot.server.Holder;
import app.socketiot.server.servers.ServerBase;
import app.socketiot.server.workers.CertificateWorker;
import app.socketiot.server.workers.DBWorker;

public class WorkerLauncher {
    public static void launch(Holder holder, ServerBase[] servers) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        if (holder.db.isConnected()) {
            scheduler.scheduleAtFixedRate(new DBWorker(holder), holder.defaults.dbSaveInterval,
                    holder.defaults.dbSaveInterval, TimeUnit.MILLISECONDS);
        }

        scheduler.scheduleAtFixedRate(new CertificateWorker(holder.sslCtxHolder), 1, 1, TimeUnit.DAYS);

        Runtime.getRuntime().addShutdownHook(new Thread(new ExitLauncher(servers, holder, scheduler)));
    }
}
