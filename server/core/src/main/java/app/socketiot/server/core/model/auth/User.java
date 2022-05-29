package app.socketiot.server.core.model.auth;

import app.socketiot.server.core.model.DashBoard;

public class User {
    public String email;

    public volatile String password;

    public volatile long lastModified;

    public DashBoard dash;

    public User(String email, String password, long lastModified, DashBoard dash) {
        this.email = email;
        this.password = password;
        this.lastModified = lastModified;
        this.dash = dash;
    }

    public User() {
    }
}
