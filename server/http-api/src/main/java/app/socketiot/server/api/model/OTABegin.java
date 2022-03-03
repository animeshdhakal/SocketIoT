package app.socketiot.server.api.model;

import java.util.List;

public class OTABegin {
    public String firmwarePath;
    public List<String> devices;
    public String blueprint_id;

    public OTABegin(String firmwarePath, List<String> devices) {
        this.firmwarePath = firmwarePath;
        this.devices = devices;
    }

    public OTABegin() {
    }
}
