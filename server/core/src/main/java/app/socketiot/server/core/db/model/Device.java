package app.socketiot.server.core.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {
    public String name;
    @JsonIgnore
    public String email;
    public String blueprint_id;
    public String token;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String json;

    public Device(String name, String email, String blueprint_id, String token, String json) {
        this.name = name;
        this.email = email;
        this.blueprint_id = blueprint_id;
        this.token = token;
        this.json = json;
    }


    public Device(String name, int user_id, String blueprint_id, String token) {
        // this(name, user_id, blueprint_id, token, {});
    }

    public Device() {
    }
}

