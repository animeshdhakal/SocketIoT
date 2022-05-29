package app.socketiot.server.core.dao;

import java.util.concurrent.ConcurrentMap;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.device.UserDevice;

public class DeviceDao {
    private ConcurrentMap<String, UserDevice> devices;

    public DeviceDao(ConcurrentMap<String, User> users) {
        for (User user : users.values()) {
            for (Device device : user.dash.devices) {
                devices.put(device.token, new UserDevice(user, device));
            }
        }
    }

    public UserDevice getUserDeviceByToken(String token) {
        return devices.get(token);
    }
}
