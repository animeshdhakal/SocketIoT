package app.socketiot.server.core.pinstore;

import java.util.ArrayList;
import java.util.List;

import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.statebase.HardwareStateBase;
import io.netty.channel.Channel;

public class MultiValuePinStore extends PinStore {
    public List<String> values;

    public MultiValuePinStore() {
    }

    public MultiValuePinStore(String defaultValue) {
        values = new ArrayList<>();
    }

    @Override
    public void updateValue(String value) {
        values.add(value);
    }

    @Override
    public void sendSync(Channel channel, int deviceID, short pin) {
        if (values.size() == 0 || channel == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        HardwareStateBase base = channel.pipeline().get(HardwareStateBase.class);

        if (base == null) {
            builder.append(deviceID).append("\0").append(pin);
        } else {
            builder.append(pin);
        }
        for (String element : values) {
            builder.append("\0").append(element);
        }

        channel.writeAndFlush(new HardwareMessage(MsgType.WRITE, builder.toString()));
    }

    @Override
    public String getValue() {
        return values.toString();
    }
}
