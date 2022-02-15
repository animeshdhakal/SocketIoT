package app.socketiot.server.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingIOHandler {
    public final ThreadPoolExecutor dbExecutor;
    public final ThreadPoolExecutor messageExecutor;

    public BlockingIOHandler() {
        this.messageExecutor = new ThreadPoolExecutor(1, 1, 2L, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1));

        this.dbExecutor = new ThreadPoolExecutor(1, 1, 2L, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(200));
    }

    public void executeDB(Runnable runnable) {
        dbExecutor.execute(runnable);
    }

    public void executeMessage(Runnable runnable) {
        messageExecutor.execute(runnable);
    }
}
