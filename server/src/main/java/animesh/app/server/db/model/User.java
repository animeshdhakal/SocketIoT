package animesh.app.server.db.model;

import animesh.app.server.utils.SHA256Util;

@Table("users")
public class User extends BaseModel {
    public String email;
    public String password;
    public String token = "";

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void hashPass() {
        this.password = SHA256Util.createHash(this.password, this.email);
    }
}
