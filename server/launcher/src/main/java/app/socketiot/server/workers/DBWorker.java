package app.socketiot.server.workers;

import java.io.Closeable;
import java.util.ArrayList;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.device.Device;

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
