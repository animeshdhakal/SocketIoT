package app.socketiot.server.workers;

import java.io.Closeable;
import java.util.ArrayList;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.model.auth.User;

public class DBWorker implements Runnable, Closeable {
    private final Holder holder;

    public DBWorker(Holder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        ArrayList<User> users = holder.userDao.getAllUsers();
        holder.userDBDao.saveAllUsers(users);
    }

    @Override
    public void close() {
        run();
    }

}
