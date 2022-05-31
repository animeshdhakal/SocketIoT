package app.socketiot.server.core.dao;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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

    public List<Device> getAllDevicesByBluePrintID(String bluePrintID) {
        return devices.values().stream().filter(userDevice -> userDevice.device.bluePrintID.equals(bluePrintID))
                .map(userDevice -> userDevice.device).collect(Collectors.toList());
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
