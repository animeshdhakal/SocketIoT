package app.socketiot.server.core.dao;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.blueprint.BluePrint;

public class BluePrintDao {
    private ConcurrentMap<String, BluePrint> bluePrints;

    public BluePrintDao(ConcurrentMap<String, User> users) {
        bluePrints = new ConcurrentHashMap<>();
        for (User user : users.values()) {
            for (BluePrint blueprint : user.dash.bluePrints) {
                bluePrints.put(blueprint.id, blueprint);
            }
        }
    }

    public BluePrint getBluePrintByID(String id) {
        return bluePrints.get(id);
    }

    public void addBluePrint(BluePrint blueprint) {
        bluePrints.put(blueprint.id, blueprint);
    }

    public void deleteBluePrintByID(String id) {
        bluePrints.remove(id);
    }
}
