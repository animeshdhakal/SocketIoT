package app.socketiot.server.core.json.model;

import java.util.concurrent.ConcurrentHashMap;

public class DeviceJson {
    public ConcurrentHashMap<Short, String> pins;

    public DeviceJson(ConcurrentHashMap<Short, String> pins) {
        this.pins = pins;
    }

    public DeviceJson() {
    }
}
