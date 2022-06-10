package app.socketiot.server.core.model.token;

public class ResetToken extends TokenBase {
    public ResetToken(String email) {
        super(email, DEFAULT_EXPIRE_TIME);
    }
}
