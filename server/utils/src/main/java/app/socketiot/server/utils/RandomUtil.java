package app.socketiot.server.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomUtil {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private static final long MSB = 0x8000000000000000L;

    public static String unique() {
        return Long.toHexString(MSB | secureRandom.nextLong()) + Long.toHexString(MSB | secureRandom.nextLong());
    }

    public static String unique(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return base64Encoder.encodeToString(bytes);
    }

}