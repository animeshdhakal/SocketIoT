package animesh.app.server.db.model;

import animesh.app.server.auth.PasswordAuthentication;

public class User {
    public String email;
    public String password;

    public User(String email, String password) {
        this.email = email;
        PasswordAuthentication auth = new PasswordAuthentication();
        this.password = auth.hash(password.toCharArray());
    }
}
