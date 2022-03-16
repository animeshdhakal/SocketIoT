package app.socketiot.server.core.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
    public String email;

    public String password;

    @JsonIgnore
    public volatile boolean isUpdated = false;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
