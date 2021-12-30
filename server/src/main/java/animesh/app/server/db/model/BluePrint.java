package animesh.app.server.db.model;

import animesh.app.server.utils.RandomUtil;

public class BluePrint {
    public String name;
    public String json;
    public String blueprint_id;
    public int user_id;

    public BluePrint(String name, String token, String json, int user_id) {
        this.name = name;
        this.json = json;
        this.blueprint_id = RandomUtil.unique(8);
        this.user_id = user_id;
    }
}
