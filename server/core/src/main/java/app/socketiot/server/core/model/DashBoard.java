package app.socketiot.server.core.model;

import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.utils.ArrayUtil;

public class DashBoard {

    public volatile Device[] devices = {};
    public volatile BluePrint[] blueprints = {};

    public Device getLastDevice() {
        if (devices.length == 0) {
            return null;
        }
        return devices[devices.length - 1];
    }

    public Device getDeviceByToken(String token) {
        for (Device device : devices) {
            if (device.token.equals(token)) {
                return device;
            }
        }
        return null;
    }

    public void addDevice(Device device) {
        devices = ArrayUtil.add(devices, device, Device.class);
    }

    public boolean removeDeviceByToken(String token) {
        for (int i = 0; i < devices.length; i++) {
            if (devices[i].token.equals(token)) {
                devices = ArrayUtil.remove(devices, i, Device.class);
                return true;
            }
        }
        return false;
    }

}
