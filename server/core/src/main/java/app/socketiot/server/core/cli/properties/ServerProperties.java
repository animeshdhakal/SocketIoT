package app.socketiot.server.core.cli.properties;

import java.io.FileReader;
import java.util.Properties;

public class ServerProperties extends Properties {
    public ServerProperties() {
        try {
            FileReader fileReader = new FileReader("server.properties");
            load(fileReader);
        } catch (Exception e) {
        }
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public int getIntProperty(String key, int defaultValue) {
        String prop = getProperty(key);
        if (prop == null || prop.isEmpty()) {
            return defaultValue;
        }
        return Integer.parseInt(prop);
    }

    public boolean getBoolProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public boolean getBoolProperty(String key, boolean defaultValue) {
        String prop = getProperty(key);
        if (prop == null || prop.isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(prop);
    }
}
