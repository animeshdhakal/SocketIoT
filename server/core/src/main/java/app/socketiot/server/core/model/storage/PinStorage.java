package app.socketiot.server.core.model.storage;

import java.util.Collection;
import io.netty.channel.Channel;

public abstract class PinStorage {
    public abstract void sendSync(Channel channel, int deviceID, short pin);

    public abstract void updateValue(String value);

    public abstract Collection<String> getValues();
}
