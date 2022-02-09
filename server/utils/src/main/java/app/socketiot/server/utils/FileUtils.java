package app.socketiot.server.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileUtils {
    public static String getPattern(Path path, String pattern) throws IOException {
        byte[] data = Files.readAllBytes(path);
        int index = KPM.indexOf(data, pattern.getBytes());
        if (index == -1) {
            throw new IOException("Pattern not found");
        }

        int i = index + pattern.length();
        while (data[i] != 0) {
            i++;
        }

        return new String(Arrays.copyOfRange(data, index + pattern.length(), i));
    }
}
