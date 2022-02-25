package app.socketiot.server.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingIOHandler {
    public final ThreadPoolExecutor dbExecutor;
    public final ThreadPoolExecutor messageExecutor;
    private final int MIN_POOL_SIZE = 2;

    public BlockingIOHandler(int poolSize) {
        poolSize = Math.max(MIN_POOL_SIZE, poolSize);
        this.messageExecutor = new ThreadPoolExecutor(poolSize / 3, poolSize / 2, 2L, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(200));

        this.dbExecutor = new ThreadPoolExecutor(poolSize / 2, poolSize, 2L, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(200));

        this.dbExecutor.allowCoreThreadTimeOut(true);
    }

    public void executeDB(Runnable runnable) {
        dbExecutor.execute(runnable);
    }

    public void execute(Runnable runnable) {
        messageExecutor.execute(runnable);
    }
}
