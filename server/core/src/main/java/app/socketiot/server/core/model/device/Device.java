package app.socketiot.server.core.model.device;

import java.util.concurrent.ConcurrentMap;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import app.socketiot.server.core.model.enums.DeviceStatus;
import app.socketiot.server.core.model.json.View;
import app.socketiot.server.core.model.storage.PinStorage;
import app.socketiot.server.core.model.storage.PinStorageDeserializer;

public class Device {
    public volatile String name;

    public volatile int id;

    public volatile String token;

    public volatile String bluePrintID;

    @JsonView(View.Protected.class)
    public volatile DeviceStatus status = DeviceStatus.Offline;

    public volatile String lastIP;

    public volatile long lastOnline;

    public HardwareInfo hardwareInfo;

    @JsonView(View.Private.class)
    @JsonDeserialize(contentUsing = PinStorageDeserializer.class)
    public ConcurrentMap<Short, PinStorage> pinStorage;

    public boolean isInvalid() {
        return name == null || name.length() > 40 || bluePrintID == null;
    }

}
