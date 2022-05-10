package app.socketiot.server.utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReadUtil {
    public static String readFileAsString(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        String data = "";

        try (InputStream is = FileReadUtil.class.getResourceAsStream(path)) {
            if (is != null) {
                data = new String(is.readAllBytes());
            }
        } catch (Exception e) {

        }

        Path p = Paths.get(System.getProperty("user.dir"), path);
        if (Files.exists(p)) {
            try (InputStream is = Files.newInputStream(p)) {
                data = new String(is.readAllBytes());
            } catch (Exception e) {

            }
        }

        return data;
    }

    public static String readVerifyUserMailBody() {
        return readFileAsString("static/verify-user.html");
    }

    public static String readResetPasswordMailBody() {
        return readFileAsString("static/reset-password.html");
    }
}
