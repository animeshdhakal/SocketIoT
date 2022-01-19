package app.socketiot.server.launcher;

import java.util.concurrent.ScheduledExecutorService;

import app.socketiot.server.core.Holder;
import app.socketiot.server.servers.BaseServer;

public class ExitLauncher implements Runnable {
    private final Holder holder;
    private final BaseServer[] servers;
    private final ScheduledExecutorService scheduler;

    public ExitLauncher(BaseServer[] servers, Holder holder, ScheduledExecutorService scheduler) {
        this.holder = holder;
        this.servers = servers;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        System.out.println("Closing Server");
        holder.close();
        for (BaseServer server : servers) {
            server.close();
        }
        scheduler.shutdown();
    }
}
