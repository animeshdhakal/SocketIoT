package app.socketiot.server.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingIOHandler {
    public final ThreadPoolExecutor dbExecutor;
    
    public BlockingIOHandler() {
        dbExecutor = new ThreadPoolExecutor(1, 1, 2L, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(200));
    }

    public void executeDB(Runnable runnable) {
        dbExecutor.execute(runnable);
    }
}
