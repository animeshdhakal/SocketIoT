package app.socketiot.server.workers;

import java.io.Closeable;
import java.util.ArrayList;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.User;

public class UserSaverWorker implements Runnable, Closeable {
    private final Holder holder;

    public UserSaverWorker(Holder holder) {
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
