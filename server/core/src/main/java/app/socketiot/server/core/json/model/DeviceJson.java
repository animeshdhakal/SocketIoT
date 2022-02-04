package app.socketiot.server.core.json.model;

import java.util.concurrent.ConcurrentHashMap;

public class DeviceJson {
    public ConcurrentHashMap<String, String> pins;

    public DeviceJson(ConcurrentHashMap<String, String> pins) {
        this.pins = pins;
    }

    public DeviceJson() {
    }
}
