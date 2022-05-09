package app.socketiot.server.utils;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SerializationUtil {
    public static void serialize(Path path, Object object) {
        try {
            OutputStream os = Files.newOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object deserialize(Path path) {
        if (Files.exists(path)) {
            try {
                InputStream is = Files.newInputStream(path);
                ObjectInputStream ois = new ObjectInputStream(is);
                return ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
