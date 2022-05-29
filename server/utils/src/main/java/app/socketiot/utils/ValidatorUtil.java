package app.socketiot.utils;

public class ValidatorUtil {
    public static boolean validateEmail(String email) {
        return email.matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$");
    }
}
