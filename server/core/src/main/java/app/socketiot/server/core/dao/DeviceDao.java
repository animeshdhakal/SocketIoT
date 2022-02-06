package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import app.socketiot.server.core.db.model.Device;

public class DeviceDao {

    private ConcurrentMap<String, Device> devices;

    public DeviceDao(ConcurrentMap<String, Device> devices) {
        this.devices = devices;
    }

    public Device getDeviceByToken(String token) {
        return devices.get(token);
    }

    public Device getDeviceByEmail(String email) {
        return devices.values().stream().filter(device -> device.email.equals(email)).findFirst().orElse(null);
    }

    public Device getDeviceByName(String name) {
        return devices.values().stream().filter(device -> device.name.equals(name)).findFirst().orElse(null);
    }

    public List<Device> getAllDevicesByEmail(String email) {
        List<Device> devicesList = devices.values().stream().filter(device -> device.email.equals(email))
                .collect(Collectors.toList());
        return devicesList;
    }

    public void addDevice(Device device) {
        device.isUpdated = true;
        devices.put(device.token, device);
    }

    public void removeDevice(String token) {
        devices.remove(token);
    }

    public void updateDevice(Device device) {
        device.isUpdated = true;
    }

    public ArrayList<Device> getAllDevices() {
        ArrayList<Device> data = new ArrayList<>();
        for (Device device : devices.values()) {
            if (device.isUpdated) {
                data.add(device);
                device.isUpdated = false;
            }
        }
        return data;
    }
}
