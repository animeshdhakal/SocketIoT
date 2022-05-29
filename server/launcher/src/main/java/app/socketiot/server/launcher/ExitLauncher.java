package app.socketiot.server.launcher;

import java.util.concurrent.ScheduledExecutorService;
import app.socketiot.server.Holder;
import app.socketiot.server.servers.ServerBase;

public class ExitLauncher implements Runnable {
    private final ScheduledExecutorService scheduler;
    private final ServerBase[] servers;
    private final Holder holder;

    public ExitLauncher(ServerBase[] servers, Holder holder, ScheduledExecutorService scheduler) {
        this.servers = servers;
        this.holder = holder;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        System.out.println("Closing Server");
        for (ServerBase server : servers) {
            server.close();
        }
        scheduler.shutdown();
        holder.close();
    }
}
