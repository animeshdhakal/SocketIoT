package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.widgets.Widget;

public class BluePrintDao {
    private ConcurrentHashMap<String, BluePrint> bluePrints;

    public BluePrintDao(ConcurrentHashMap<String, BluePrint> bluePrints) {
        this.bluePrints = bluePrints;
    }

    public BluePrint getBluePrint(String blueprintId) {
        return bluePrints.get(blueprintId);
    }

    public BluePrint getBluePrintByEmailAndID(String email, String blueprintId) {
        BluePrint bluePrint = this.getBluePrint(blueprintId);
        if (bluePrint == null) {
            return null;
        }
        return bluePrint.email.equals(email) ? bluePrint : null;
    }

    public void addBluePrint(BluePrint bluePrint) {
        bluePrint.isUpdated = true;
        bluePrints.put(bluePrint.id, bluePrint);
    }

    public void removeBluePrint(String blueprintId) {
        bluePrints.remove(blueprintId);
    }

    public void updateBluePrint(BluePrint bluePrint) {
        bluePrint.isUpdated = true;
    }

    public BluePrint getBluePrintByEmail(String email) {
        return bluePrints.values().stream().filter(blueprint -> blueprint.email.equals(email)).findFirst().orElse(null);
    }

    public List<BluePrint> getAllBluePrintsByEmail(String email) {
        List<BluePrint> bluePrintsList = bluePrints.values().stream().filter(blueprint -> blueprint.email.equals(email))
                .collect(Collectors.toList());
        return bluePrintsList;
    }

    public boolean replaceWidgets(String email, String blueprint_id, List<Widget> widgets) {
        BluePrint bluePrint = bluePrints.get(blueprint_id);
        if (bluePrint == null || !bluePrint.email.equals(email)) {
            return false;
        }
        bluePrint.json.widgets = widgets;
        updateBluePrint(bluePrint);
        return true;
    }

    public ArrayList<BluePrint> getAllBluePrints() {
        ArrayList<BluePrint> data = new ArrayList<>();
        for (BluePrint bluePrint : bluePrints.values()) {
            if (bluePrint.isUpdated) {
                data.add(bluePrint);
                bluePrint.isUpdated = false;
            }
        }
        return data;
    }

}
