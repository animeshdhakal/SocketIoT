package app.socketiot.server.utils;

import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Path;

public class HardwareInfoUtil {
    public static String getPatternFromPath(Path path, String pattern) {
        try {
            byte[] data = Files.readAllBytes(path);
            int index = KPM.indexOf(data, pattern.getBytes());
            if (index == -1) {
                return null;
            }

            int start = index + pattern.length();
            index += pattern.length();

            while (data[index] != '\0') {
                index++;
            }
            return new String(Arrays.copyOfRange(data, start, index));
        } catch (Exception e) {
            return null;
        }

    }
}
