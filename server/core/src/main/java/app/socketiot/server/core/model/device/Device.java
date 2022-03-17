package app.socketiot.server.core.model.device;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import app.socketiot.server.core.json.model.DeviceJson;
import app.socketiot.server.core.model.HardwareInfo;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

@JsonFilter("DeviceJsonFilter")
public class Device {
    public volatile String name;

    public volatile int id = -1;

    @JsonIgnore
    public volatile String email;

    public volatile String blueprint_id;

    public volatile String token;

    public DeviceJson json;

    public volatile HardwareInfo info;

    public volatile Boolean online = false;

    public volatile String lastIP;

    public volatile long lastOnline;

    @JsonIgnore
    public volatile boolean isUpdated = false;

    @JsonIgnore
    public Set<Channel> hardGroup = ConcurrentHashMap.newKeySet();

    @JsonIgnore
    public Set<Channel> appGroup = ConcurrentHashMap.newKeySet();

    public Device(String name, String email, String blueprint_id, String token, int id, DeviceJson json) {
        this.name = name;
        this.email = email;
        this.blueprint_id = blueprint_id;
        this.token = token;
        this.json = json;
    }

    public Device(String token) {
        this.token = token;
    }

    public Device() {
    }

    public void sendToApps(ChannelHandlerContext ctx, HardwareMessage message) {
        for (Channel channel : appGroup) {
            if(!ctx.channel().equals(channel)){
                channel.writeAndFlush(message);
            }
        }
    }

    public void sendToHardware(ChannelHandlerContext ctx, HardwareMessage message) {
        for (Channel channel : hardGroup) {
            if(!ctx.channel().equals(channel)){
                channel.writeAndFlush(message);
            }
        }
    }

    public boolean updatePin(ChannelHandlerContext ctx, String pinn, String value) {
        short pin = NumberUtil.parsePin(pinn);
        if (json.pins.containsKey(pin)) {
            json.pins.put(pin, value);
            isUpdated = true;
            sendToHardware(ctx, new HardwareMessage(MsgType.WRITE, pinn, value));
            sendToApps(ctx, new HardwareMessage(MsgType.WRITE, String.valueOf(id), pinn, value));
            return true;
        }
        return false;
    }
}
