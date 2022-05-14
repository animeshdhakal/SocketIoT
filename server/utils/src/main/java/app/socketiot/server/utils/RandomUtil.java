package app.socketiot.server.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomUtil {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String unique(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return base64Encoder.encodeToString(bytes);
    }

}