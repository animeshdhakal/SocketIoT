package app.socketiot.server.core.model.blueprint;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import app.socketiot.server.core.json.model.BluePrintJson;

@JsonFilter("BluePrintJsonFilter")
public class BluePrint {
    public volatile String name;

    @JsonIgnore
    public volatile String email;

    public volatile String id;

    public BluePrintJson json;

    @JsonIgnore
    public volatile boolean isUpdated = false;

    public BluePrint(String name, String email, String id, BluePrintJson json) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.json = json;
    }

    public BluePrint(String id) {
        this.id = id;
    }

    public BluePrint() {

    }
}
