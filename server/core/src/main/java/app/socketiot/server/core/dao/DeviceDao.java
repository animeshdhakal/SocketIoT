package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.device.UserDevice;

public class DeviceDao {

    private ConcurrentMap<String, UserDevice> devices;

    public DeviceDao(ConcurrentMap<String, User> users) {
        this.devices = new ConcurrentHashMap<>();
        for (User user : users.values()) {
            for (Device device : user.json.devices) {
                devices.put(device.token, new UserDevice(user, device));
            }
        }
    }

    public List<Device> getAllDevicesByBlueprint(String blueprintId) {
        ArrayList<Device> data = new ArrayList<>();
        for (UserDevice userDevice : devices.values()) {
            if (userDevice.device.blueprint_id.equals(blueprintId)) {
                data.add(userDevice.device);
            }
        }
        return data;
    }

    public void addDevice(User user, Device device) {
        devices.put(device.token, new UserDevice(user, device));
    }

    public Device getDevice(String token) {
        return devices.get(token).device;
    }

    public UserDevice getUserDevice(String token) {
        return devices.get(token);
    }

    public void removeDevice(String token) {
        devices.remove(token);
    }

    public void sendToHardware(int deviceId, HardwareMessage message) {
        for (UserDevice userDevice : devices.values()) {
            if (userDevice.device.id == deviceId) {
                userDevice.user.json.sendToHardware(null, deviceId, message);
            }
        }
    }

}
