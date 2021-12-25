package animesh.app.server.db.model;

import animesh.app.server.auth.PasswordAuthentication;

public class User {
    public String email;
    public String password;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        hashPass();
    }

    public void hashPass() {
        PasswordAuthentication auth = new PasswordAuthentication();
        this.password = auth.hash(this.password.toCharArray());
    }
}
