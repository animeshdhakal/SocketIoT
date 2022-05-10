package app.socketiot.server.utils;

import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern emailPattern = Pattern
            .compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public static boolean validateEmail(String email) {
        if (emailPattern.matcher(email).matches()) {
            return true;
        }
        return false;
    }
}
