package app.socketiot.server.api.model;

import java.util.List;

import app.socketiot.server.core.model.blueprint.BluePrint;

public class BluePrintList {
    public List<BluePrint> bluePrints;

    public BluePrintList(List<BluePrint> bluePrints) {
        this.bluePrints = bluePrints;
    }

    public BluePrintList() {
    }
}
