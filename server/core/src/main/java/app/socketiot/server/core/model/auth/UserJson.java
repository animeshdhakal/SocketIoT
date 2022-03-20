package app.socketiot.server.core.model.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.widgets.Widget;
import app.socketiot.server.core.statebase.HardwareStateBase;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class UserJson {
    public volatile Device[] devices = {};
    public volatile BluePrint[] blueprints = {};

    @JsonIgnore
    public volatile boolean isUpdated = false;

    @JsonIgnore
    public Set<Channel> hardChannels = new HashSet<>();

    @JsonIgnore
    public Set<Channel> appChannels = new HashSet<>();

    public UserJson() {
    }

    public void addDevice(Device device) {
        Device[] ndevices = new Device[devices.length + 1];
        for (int i = 0; i < devices.length; i++) {
            ndevices[i] = devices[i];
        }
        ndevices[devices.length] = device;
        devices = ndevices;
    }

    public void addBluePrint(BluePrint blueprint) {
        BluePrint[] nblueprints = new BluePrint[blueprints.length + 1];
        for (int i = 0; i < blueprints.length; i++) {
            nblueprints[i] = blueprints[i];
        }
        nblueprints[blueprints.length] = blueprint;
        blueprints = nblueprints;
    }

    public boolean removeDevice(String token) {
        for (int i = 0; i < devices.length; i++) {
            if (devices[i].token.equals(token)) {
                Device[] ndevices = new Device[devices.length - 1];
                for (int j = 0; j < i; j++) {
                    ndevices[j] = devices[j];
                }
                for (int j = i; j < devices.length - 1; j++) {
                    ndevices[j] = devices[j + 1];
                }
                devices = ndevices;
                return true;
            }
        }
        return false;
    }

    public boolean removeBlueprint(String id) {
        for (int i = 0; i < blueprints.length; i++) {
            if (blueprints[i].id.equals(id)) {
                BluePrint[] nblueprints = new BluePrint[blueprints.length - 1];
                for (int j = 0; j < i; j++) {
                    nblueprints[j] = blueprints[j];
                }
                for (int j = i; j < blueprints.length - 1; j++) {
                    nblueprints[j] = blueprints[j + 1];
                }
                blueprints = nblueprints;
                return true;
            }
        }
        return false;
    }

    public Device getLastDevice() {
        if (devices.length > 0) {
            return devices[devices.length - 1];
        }
        return null;
    }

    public BluePrint getLastBlueprint() {
        if (blueprints.length > 0) {
            return blueprints[blueprints.length - 1];
        }
        return null;
    }

    public BluePrint getBlueprint(String id) {
        for (BluePrint blueprint : blueprints) {
            if (blueprint.id.equals(id)) {
                return blueprint;
            }
        }
        return null;
    }

    public Device getDevice(String token) {
        for (Device device : devices) {
            if (device.token.equals(token)) {
                return device;
            }
        }
        return null;
    }

    public Device getDevice(int id) {
        for (Device device : devices) {
            if (device.id == id) {
                return device;
            }
        }
        return null;
    }

    public boolean replaceWidgets(String blueprint_id, List<Widget> widgets) {
        BluePrint bluePrint = getBlueprint(blueprint_id);
        if (bluePrint == null) {
            return false;
        }
        bluePrint.widgets = widgets;
        return true;
    }

    public void addHardChannel(Channel channel) {
        hardChannels.add(channel);
    }

    public void addAppChannel(Channel channel) {
        appChannels.add(channel);
    }

    public void removeHardChannel(Channel channel) {
        hardChannels.remove(channel);
    }

    public void removeAppChannel(Channel channel) {
        appChannels.remove(channel);
    }

    public void sendToApps(ChannelHandlerContext ctx, HardwareMessage message) {
        for (Channel channel : appChannels) {
            if (!channel.equals(ctx.channel())) {
                channel.writeAndFlush(message);
            }
        }
    }

    public void sendToHardware(ChannelHandlerContext ctx, int deviceID, HardwareMessage message) {
        for (Channel channel : hardChannels) {
            if (!channel.equals(ctx.channel())) {
                HardwareStateBase state = channel.pipeline().get(HardwareStateBase.class);
                if (state != null && state.getUserDevice().device.id == deviceID) {
                    channel.writeAndFlush(message);
                }
            }
        }
    }

    public int getHardwareChannelCount(int deviceID) {
        int i = 0;
        for (Channel channel : hardChannels) {
            HardwareStateBase state = channel.pipeline().get(HardwareStateBase.class);
            if (state != null && state.getUserDevice().device.id == deviceID) {
                i++;
            }
        }
        return i;
    }

}
