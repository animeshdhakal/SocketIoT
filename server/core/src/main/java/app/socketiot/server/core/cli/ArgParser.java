package app.socketiot.server.core.cli;

public class ArgParser {
    String args[] = null;

    public ArgParser(String[] args) {
        this.args = args;
    }

    public String getArg(String key) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(key)) {
                return args[i + 1];
            }
        }
        return null;
    }

    public boolean hasArg(String key) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(key)) {
                return true;
            }
        }
        return false;
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
