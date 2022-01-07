package animesh.app.server.db.model;

import animesh.app.server.utils.RandomUtil;

@Table("blueprints")
public class Device extends BaseModel {
    public String device_id;
    public String name;
    public String blueprint_id;
    public String token;
    public String json;
    public int user_id;

    public Device(String name, String token, String json, String blueprint_id, int user_id) {
        this.name = name;
        this.token = token;
        this.json = json;
        this.user_id = user_id;
        this.blueprint_id = blueprint_id;
    }

    public Device(String name, String json, String blueprint_id, int user_id) {
        this.name = name;
        this.token = RandomUtil.unique();
        this.json = json;
        this.user_id = user_id;
        this.blueprint_id = blueprint_id;
    }
}