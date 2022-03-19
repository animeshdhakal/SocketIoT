package app.socketiot.server.core.model.device;

import java.util.concurrent.ConcurrentMap;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import app.socketiot.server.core.model.HardwareInfo;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;

@JsonFilter("DeviceJsonFilter")
public class Device {
    public volatile String name;

    public volatile int id = -1;

    public volatile String blueprint_id;

    public volatile String token;

    public ConcurrentMap<Short, String> pins;

    public volatile HardwareInfo info;

    @JsonIgnore
    public volatile Boolean online = false;

    public volatile String lastIP;

    public volatile long lastOnline;

    public Device(String name, String blueprint_id, String token, int id, ConcurrentMap<Short, String> pins) {
        this.name = name;
        this.blueprint_id = blueprint_id;
        this.token = token;
        this.pins = pins;
    }

    public Device(String token) {
        this.token = token;
    }

    public Device() {
    }

    public boolean updatePin(ChannelHandlerContext ctx, String pinn, String value) {
        short pin = NumberUtil.parsePin(pinn);
        if (pins.containsKey(pin)) {
            pins.put(pin, value);
            return true;
        }
        return false;
    }
}
