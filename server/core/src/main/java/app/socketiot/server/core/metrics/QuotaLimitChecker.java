package app.socketiot.server.core.metrics;

public class QuotaLimitChecker {
    private long count;
    private long lastTick;
    private final int limit;

    public QuotaLimitChecker(int limit) {
        this.limit = limit;
        this.count = 0;
        this.lastTick = System.currentTimeMillis();
    }

    public boolean quotaExceeded() {
        long now = System.currentTimeMillis();
        if (now - lastTick > 1000) {
            count = 0;
            lastTick = now;
        }
        count++;
        return count > limit;
    }

    public int getRequestsPerSecond() {
        return (int) (count / (System.currentTimeMillis() - lastTick) * 1000);
    }

}
