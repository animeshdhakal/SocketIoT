package app.socketiot.server.core.model.device;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import app.socketiot.server.core.json.model.DeviceJson;
import io.netty.channel.Channel;

@JsonFilter("DeviceJsonFilter")
public class Device {
    public String name;
    @JsonIgnore
    public String email;
    public String blueprint_id;
    public String token;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public DeviceJson json;
    public Boolean online = false;
    public String lastIP;
    @JsonIgnore
    public volatile boolean isUpdated = false;
    @JsonIgnore
    public Set<Channel> hardGroup = ConcurrentHashMap.newKeySet();
    @JsonIgnore
    public Set<Channel> dashGroup = ConcurrentHashMap.newKeySet();

    public Device(String name, String email, String blueprint_id, String token, DeviceJson json) {
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
}
