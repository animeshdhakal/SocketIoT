package animesh.app.server.db.model;

public class Project {
    public String name;
    public String token;
    public String json;
    public int userId;

    public Project(String name, String token, String json, int userId) {
        this.name = name;
        this.token = token;
        this.json = json;
        this.userId = userId;
    }
}
