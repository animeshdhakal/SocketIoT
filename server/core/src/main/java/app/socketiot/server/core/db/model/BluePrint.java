package app.socketiot.server.core.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BluePrint {
    public String name;
    @JsonIgnore
    public String email;
    public String id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String json;

    public BluePrint(String name, String email, String id, String json) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.json = json;
    }

    public BluePrint() {

    }
}
