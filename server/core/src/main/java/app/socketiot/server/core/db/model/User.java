package app.socketiot.server.core.db.model;

public class User {
    public String email;
    public String password;
    public String refreshToken;
    public int id;

    public User() {
    }

    public User(String email, String password, int id) {
        this.email = email;
        this.password = password;
        this.id = id;
    }

}
