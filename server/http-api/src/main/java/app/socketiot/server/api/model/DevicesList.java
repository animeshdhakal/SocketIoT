package app.socketiot.server.api.model;

import java.util.List;

import app.socketiot.server.core.db.model.Device;

public class DevicesList {
    public List<Device> devices;

    public DevicesList(List<Device> devices) {
        this.devices = devices;
    }

    public DevicesList() {
    }
}
