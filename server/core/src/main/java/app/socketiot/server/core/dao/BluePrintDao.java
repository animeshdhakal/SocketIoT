package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import app.socketiot.server.core.db.model.BluePrint;
import app.socketiot.server.core.json.model.BluePrintJson;
import app.socketiot.server.core.json.model.Widget;

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

    public Widget getWidget(String email, String blueprintId, long widgetId) {
        BluePrint bluePrint = bluePrints.get(blueprintId);
        if (bluePrint == null || bluePrint.email.equals(email)) {
            return null;
        }
        return bluePrint.json.widgets.stream().filter(widget -> widget.id == widgetId).findFirst().orElse(null);
    }

    public void updateWidget(String email, String blueprintId, long widgetId, Widget widget) {
        BluePrint bluePrint = bluePrints.get(blueprintId);
        if (bluePrint == null || bluePrint.email.equals(email)) {
            return;
        }
        bluePrint.json.widgets.stream().filter(w -> w.id == widgetId).findFirst().ifPresent(w -> {
            w.height = widget.height;
            w.width = widget.width;
            w.x = widget.x;
            w.y = widget.y;
            w.pin = widget.pin;
        });
        updateBluePrint(bluePrint);
    }

    public boolean addWidget(String email, String blueprint_id, Widget widget) {
        BluePrint bluePrint = bluePrints.get(blueprint_id);
        if (bluePrint != null && bluePrint.email.equals(email)) {
            if (bluePrint.json == null || bluePrint.json.widgets == null) {
                bluePrint.json = new BluePrintJson();
                bluePrint.json.widgets = new ArrayList<Widget>();
            }
            if (bluePrint.json.widgets.size() > 0) {
                widget.id = bluePrint.json.widgets.get(bluePrint.json.widgets.size() - 1).id + 1;
            } else {
                widget.id = 1;
            }
            bluePrint.json.widgets.add(widget);

            updateBluePrint(bluePrint);

            return true;
        }
        return false;
    }

    public boolean removeWidget(String email, String blueprint_id, long widgetId) {
        BluePrint bluePrint = bluePrints.get(blueprint_id);
        if (bluePrint != null && bluePrint.email.equals(email)) {
            if (!bluePrint.json.widgets.removeIf(w -> w.id == widgetId)) {
                return false;
            }
            updateBluePrint(bluePrint);
            return true;
        }
        return false;
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
