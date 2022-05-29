package app.socketiot.server.workers;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import app.socketiot.server.Holder;
import app.socketiot.server.core.model.auth.User;

public class DBWorker implements Runnable, Closeable {
    private final Holder holder;
    private long lastRan;

    public DBWorker(Holder holder) {
        this.holder = holder;
        this.lastRan = System.currentTimeMillis();
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        List<User> usersList = new ArrayList<>();
        for (User user : holder.userDao.getAllUsers().values()) {
            if (lastRan <= user.lastModified) {
                usersList.add(user);
            }
        }
        holder.userDBDao.saveAllUsers(usersList);
        lastRan = now;
    }

    @Override
    public void close() {
        run();
    }

}
