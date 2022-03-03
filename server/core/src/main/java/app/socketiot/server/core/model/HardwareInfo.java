package app.socketiot.server.core.model;

public class HardwareInfo {
    public String firmwareVersion;
    public String libVersion;
    public String cpu;
    public String board;
    public int heartbeat;
    public String blueprintid;
    public String build;

    public void addInfo(String key, String value) {
        switch (key) {
            case "hbeat":
                this.heartbeat = Integer.parseInt(value);
                break;
            case "fv":
                this.firmwareVersion = value;
                break;
            case "build":
                this.build = value;
                break;
            case "lv":
                this.libVersion = value;
                break;
            case "cpu":
                this.cpu = value;
                break;
            case "board":
                this.board = value;
                break;
            case "bid":
                this.blueprintid = value;
                break;
        }
    }

    public HardwareInfo(String[] data) {
        for (int i = 0; i < data.length; i++) {
            if (i < data.length - 1) {
                addInfo(data[i], data[++i]);
            }
        }
    }

}
