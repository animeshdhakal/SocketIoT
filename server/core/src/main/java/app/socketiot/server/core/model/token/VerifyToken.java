package app.socketiot.server.core.model.token;

public class VerifyToken extends TokenBase {
    public final String password;

    public VerifyToken(String email, String password) {
        super(email, DEFAULT_EXPIRE_TIME);
        this.password = password;
    }
}
