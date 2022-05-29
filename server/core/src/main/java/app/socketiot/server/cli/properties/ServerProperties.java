package app.socketiot.server.cli.properties;

import java.io.FileReader;
import java.util.Properties;

public class ServerProperties extends Properties {
    public ServerProperties() {
        try {
            FileReader reader = new FileReader("server.properties");
            load(reader);
        } catch (Exception e) {
        }
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }
}
