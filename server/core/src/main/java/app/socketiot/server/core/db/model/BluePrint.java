package app.socketiot.server.core.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BluePrint {
    public String name;
    @JsonIgnore
    public String email;
    public String id;
    public String json;

    public BluePrint(String name, String email, String id, String json) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.json = json;
    }


    public BluePrint(String id){
        this.id = id;
    }

    public BluePrint() {

    }
}
