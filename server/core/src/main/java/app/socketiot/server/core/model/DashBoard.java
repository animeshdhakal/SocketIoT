package app.socketiot.server.core.model;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.core.model.statebase.HardwareStateBase;
import app.socketiot.utils.ArrayUtil;
import io.netty.channel.Channel;

public class DashBoard {
    public volatile Device[] devices = {};

    public volatile BluePrint[] bluePrints = {};

    @JsonIgnore
    public Set<Channel> appChannels = new HashSet<>();

    @JsonIgnore
    public Set<Channel> hardChannels = new HashSet<>();

    public void addAppChannel(Channel channel) {
        appChannels.add(channel);
    }

    public void addHardChannel(Channel channel) {
        hardChannels.add(channel);
    }

    public void removeAppChannel(Channel channel) {
        appChannels.remove(channel);
    }

    public void removeHardChannel(Channel channel) {
        hardChannels.remove(channel);
    }

    public void sendToApps(Channel channel, InternalMessage message) {
        for (Channel appChannel : appChannels) {
            if (appChannel != channel) {
                appChannel.writeAndFlush(message);
            }
        }
    }

    public void sendToHardwares(Channel channel, int deviceID, InternalMessage message) {
        for (Channel hardChannel : hardChannels) {
            HardwareStateBase state = channel.pipeline().get(HardwareStateBase.class);
            if (hardChannel != channel && state != null && state.getDevice().id == deviceID) {
                hardChannel.writeAndFlush(message);
            }
        }
    }

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

    public Device getDeviceByID(int id) {
        for (Device device : devices) {
            if (device.id == id) {
                return device;
            }
        }
        return null;
    }

    public BluePrint getBluePrintByID(String id) {
        for (BluePrint bluePrint : bluePrints) {
            if (bluePrint.id.equals(id)) {
                return bluePrint;
            }
        }
        return null;
    }

    public void addDevice(Device device) {
        devices = ArrayUtil.add(devices, device, Device.class);
    }

    public void addBluePrint(BluePrint bluePrint) {
        bluePrints = ArrayUtil.add(bluePrints, bluePrint, BluePrint.class);
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

    public boolean deleteBluePrintByID(String id) {
        for (int i = 0; i < bluePrints.length; i++) {
            if (bluePrints[i].id.equals(id)) {
                bluePrints = ArrayUtil.remove(bluePrints, i, BluePrint.class);
                return true;
            }
        }
        return false;
    }

}
