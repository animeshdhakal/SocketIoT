package app.socketiot.server.core.model.device;

import com.fasterxml.jackson.annotation.JsonView;
import app.socketiot.server.core.model.enums.DeviceStatus;
import app.socketiot.server.core.model.json.View;

public class Device {
    public volatile String name;

    public volatile int id;

    public volatile String token;

    @JsonView(View.Protected.class)
    public volatile DeviceStatus status = DeviceStatus.Offline;

    public volatile String lastIP;

    public volatile long lastOnline;

    public HardwareInfo hardwareInfo;

    public boolean isInvalid() {
        return name == null || name.length() > 40;
    }

}
