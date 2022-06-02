package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.device.UserDevice;

public class DeviceDao {
    private ConcurrentMap<String, UserDevice> devices;

    public DeviceDao(ConcurrentMap<String, User> users) {
        devices = new ConcurrentHashMap<>();
        for (User user : users.values()) {
            for (Device device : user.dash.devices) {
                devices.put(device.token, new UserDevice(user, device));
            }
        }
    }

    public List<Device> getAllDevicesByBluePrintID(String bluePrintID) {
        List<Device> bluePrintDevices = new ArrayList<>();
        for (UserDevice userDevice : devices.values()) {
            if (userDevice.device.bluePrintID.equals(bluePrintID)) {
                bluePrintDevices.add(userDevice.device);
            }
        }
        return bluePrintDevices;
    }

    public UserDevice getUserDeviceByToken(String token) {
        return devices.get(token);
    }

    public void addUserDevice(UserDevice userDevice) {
        devices.put(userDevice.device.token, userDevice);
    }

    public void removeUserDeviceByToken(String token) {
        devices.remove(token);
    }
}
