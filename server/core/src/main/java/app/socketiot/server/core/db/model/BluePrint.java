package app.socketiot.server.core.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import app.socketiot.server.core.json.model.BluePrintJson;

public class BluePrint {
    public String name;
    @JsonIgnore
    public String email;
    public String id;
    public BluePrintJson json;

    public BluePrint(String name, String email, String id, BluePrintJson json) {
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
