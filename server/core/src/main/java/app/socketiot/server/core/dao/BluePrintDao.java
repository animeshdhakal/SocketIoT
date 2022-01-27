package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import app.socketiot.server.core.db.model.BluePrint;

public class BluePrintDao {
    private ConcurrentHashMap<String, BluePrint> bluePrints;

    public BluePrintDao(ConcurrentHashMap<String, BluePrint> bluePrints) {
        this.bluePrints = bluePrints;
    }

    public BluePrint getBluePrint(String blueprintId) {
        return bluePrints.get(blueprintId);
    }

    public void addBluePrint(BluePrint bluePrint) {
        bluePrints.put(bluePrint.blueprint_id, bluePrint);
    }

    public void removeBluePrint(String blueprintId) {
        bluePrints.remove(blueprintId);
    }

    public void updateBluePrint(BluePrint bluePrint) {
        bluePrints.put(bluePrint.blueprint_id, bluePrint);
    }

    public ArrayList<BluePrint> getAllBluePrints() {
        ArrayList<BluePrint> data = new ArrayList<>();
        for (BluePrint bluePrint : bluePrints.values()) {
            data.add(bluePrint);
        }
        return data;
    }

}
