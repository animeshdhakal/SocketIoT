package app.socketiot.server.cli;

public class ArgParser {
    private String[] args;

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
}
