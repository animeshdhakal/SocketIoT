package app.socketiot.server.core.pinstore;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.netty.channel.Channel;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultiValuePinStore.class, name = "M"),
        @JsonSubTypes.Type(value = SingleValuePinStore.class, name = "S")
})
public abstract class PinStore {
    public abstract void updateValue(String value);

    public abstract void sendSync(Channel channel, int deviceid, short pin);

    public abstract String getValue();
}
