package app.socketiot.server.core.db.model;

public class BluePrint {
    public String name;
    public String email;
    public String blueprint_id;
    public String json;

    public BluePrint(String name, String email, String blueprint_id, String json) {
        this.name = name;
        this.email = email;
        this.blueprint_id = blueprint_id;
        this.json = json;
    }
}
