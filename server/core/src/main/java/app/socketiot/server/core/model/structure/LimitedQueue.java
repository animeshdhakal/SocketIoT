package app.socketiot.server.core.model.structure;

import java.util.concurrent.LinkedBlockingQueue;

public class LimitedQueue<T> extends LinkedBlockingQueue<T> {
    public final int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(T obj) {
        if (size() >= limit) {
            super.poll();
        }
        return super.add(obj);
    }
}
