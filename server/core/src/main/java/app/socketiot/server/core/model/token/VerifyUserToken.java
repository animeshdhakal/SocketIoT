package app.socketiot.server.core.model.token;

public class VerifyUserToken extends TokenBase {
    public String password;

    public VerifyUserToken(String email, String password, long expire) {
        super(email, expire);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
