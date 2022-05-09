package app.socketiot.server.core.model.token;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public abstract class TokenBase implements Serializable {
    public String email;
    public long expire;
    public static final long DEFAULT_EXPIRE_TIME = TimeUnit.DAYS.toMillis(1);

    public TokenBase(String email, long expire) {
        this.email = email;
        this.expire = System.currentTimeMillis() + expire;
    }

    public boolean isExpired(long now) {
        return now > expire;
    }

    @Override
    public String toString() {
        return "TokenBase{" +
                "email='" + email + '\'' + email
                + ", expire=" + expire +
                '}';
    }
}
