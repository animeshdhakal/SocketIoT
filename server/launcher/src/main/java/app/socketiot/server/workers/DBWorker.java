package app.socketiot.server.workers;

import java.io.Closeable;
import java.util.ArrayList;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.BluePrint;
import app.socketiot.server.core.db.model.Device;
import app.socketiot.server.core.db.model.User;

public class DBWorker implements Runnable, Closeable {
    private final Holder holder;

    public DBWorker(Holder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        ArrayList<User> users = holder.userDao.getAllUsers();
        ArrayList<Device> devices = holder.deviceDao.getAllDevices();
        ArrayList<BluePrint> bluePrints = holder.bluePrintDao.getAllBluePrints();
        holder.userDBDao.saveAllUsers(users);
        holder.deviceDBDao.saveAllDevices(devices);
        holder.bluePrintDBDao.saveAllBluePrints(bluePrints);
    }

    @Override
    public void close() {
        run();
    }

}
