package app.socketiot.server.core.model.token;

public class ResetToken extends TokenBase {
    public ResetToken(String email, long expire) {
        super(email, expire);
    }
}
