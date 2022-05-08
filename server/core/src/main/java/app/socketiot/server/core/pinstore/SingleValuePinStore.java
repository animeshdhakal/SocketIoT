package app.socketiot.server.core.pinstore;

import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.statebase.HardwareStateBase;
import io.netty.channel.Channel;

public class SingleValuePinStore extends PinStore {
    public volatile String value;

    public SingleValuePinStore(String defaultValue) {
        this.value = defaultValue;
    }

    public SingleValuePinStore() {
    }

    public void updateValue(String value) {
        this.value = value;
    }

    @Override
    public void sendSync(Channel channel, int deviceID, short pin) {
        HardwareStateBase base = channel.pipeline().get(HardwareStateBase.class);
        if (base == null) {
            channel.writeAndFlush(
                    new HardwareMessage(MsgType.WRITE, String.valueOf(deviceID), String.valueOf(pin), value));
        } else {
            channel.writeAndFlush(
                    new HardwareMessage(MsgType.WRITE, String.valueOf(pin), value));
        }
    }

    @Override
    public String getValue() {
        return value;
    }
}
