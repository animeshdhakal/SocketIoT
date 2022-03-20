package app.socketiot.server.core.dao;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.blueprint.BluePrint;

public class BluePrintDao {
    private ConcurrentMap<String, BluePrint> bluePrints;

    public BluePrintDao(ConcurrentMap<String, User> users) {
        this.bluePrints = new ConcurrentHashMap<>();
        for (User user : users.values()) {
            for (BluePrint bluePrint : user.json.blueprints) {
                bluePrints.put(bluePrint.id, bluePrint);
            }
        }
    }

    public BluePrint getBluePrint(String blueprintId) {
        return bluePrints.get(blueprintId);
    }

    public void addBluePrint(BluePrint bluePrint) {
        bluePrints.put(bluePrint.id, bluePrint);
    }

    public void removeBluePrint(String blueprintId) {
        bluePrints.remove(blueprintId);
    }

}
