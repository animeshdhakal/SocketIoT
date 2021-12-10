package animesh.app.server;

import java.util.Arrays;
import java.util.List;

public class ArgParser {

    List<String> vals = null;

    public ArgParser(String[] args) {
        vals = Arrays.asList(args);
    }

    public String getArg(String key) {
        int index = vals.indexOf(key);
        if (index == -1 && index == vals.size() - 1) {
            return null;
        }
        return vals.get(index + 1);
    }

    public boolean hasArg(String key) {
        return vals.contains(key);
    }

    public int getInt(String key, int defaultValue) {
        String arg = getArg(key);
        if (arg == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
