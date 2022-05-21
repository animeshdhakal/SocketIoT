package app.socketiot.server.core.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
    public String email;

    public String password;

    public String token;

    public Dashboard dash;

    @JsonIgnore
    public volatile boolean isUpdated = false;

    public User() {
    }

    public User(String email, String password, String token, Dashboard dash) {
        this.email = email;
        this.password = password;
        this.dash = dash;
        this.token = token;
    }

    public void updated() {
        isUpdated = true;
    }

}
