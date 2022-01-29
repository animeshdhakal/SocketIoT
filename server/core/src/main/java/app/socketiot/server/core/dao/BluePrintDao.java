package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import app.socketiot.server.core.db.model.BluePrint;
import app.socketiot.server.core.json.JsonParser;
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

    public void addBluePrint(BluePrint bluePrint) {
        bluePrints.put(bluePrint.id, bluePrint);
    }

    public void removeBluePrint(String blueprintId) {
        bluePrints.remove(blueprintId);
    }

    public void updateBluePrint(BluePrint bluePrint) {
        bluePrints.put(bluePrint.id, bluePrint);
    }

    public BluePrint getBluePrintByName(String name) {
        return bluePrints.values().stream().filter(blueprint -> blueprint.name.equals(name)).findFirst().orElse(null);
    }

    public BluePrint getBluePrintByEmail(String email) {
        return bluePrints.values().stream().filter(blueprint -> blueprint.email.equals(email)).findFirst().orElse(null);
    }

    public List<BluePrint> getAllBluePrintsByEmail(String email) {
        return bluePrints.values().stream().filter(blueprint -> blueprint.email.equals(email))
                .collect(Collectors.toList());
    }


    public Widget getWidget(String email, String blueprintId, long widgetId) {
        BluePrint bluePrint = bluePrints.get(blueprintId);
        if (bluePrint == null || bluePrint.email.equals(email)) {
            return null;
        }
        BluePrintJson bluePrintJson = JsonParser.parse(BluePrintJson.class, bluePrint.json);
        return bluePrintJson.widgets.stream().filter(widget -> widget.id == widgetId).findFirst().orElse(null);
    }


    public void updateWidget(String email, String blueprintId, long widgetId, Widget widget) {
        BluePrint bluePrint = bluePrints.get(blueprintId);
        if (bluePrint == null || bluePrint.email.equals(email)) {
            return;
        }
        BluePrintJson bluePrintJson = JsonParser.parse(BluePrintJson.class, bluePrint.json);
        bluePrintJson.widgets.stream().filter(w -> w.id == widgetId).findFirst().ifPresent(w -> {
            w.height = widget.height;
            w.width = widget.width;
            w.x = widget.x;
            w.y = widget.y;
            w.pin = widget.pin;    
        });
        bluePrint.json = JsonParser.toString(bluePrintJson);
        updateBluePrint(bluePrint);
    }

    public boolean addWidget(String email, String blueprint_id, Widget widget) {
        BluePrint bluePrint = bluePrints.get(blueprint_id);
        if (bluePrint != null && bluePrint.email.equals(email)) {
            BluePrintJson bluePrintJson = JsonParser.parse(BluePrintJson.class, bluePrint.json);
            if(bluePrintJson == null){
                bluePrintJson = new BluePrintJson();
                bluePrintJson.widgets = new ArrayList<Widget>();
            }
            if(bluePrintJson.widgets.size() > 0 ){
                widget.id = bluePrintJson.widgets.get(bluePrintJson.widgets.size() - 1).id + 1;
            }else{
                widget.id = 1;
            }
            bluePrintJson.widgets.add(widget);

            bluePrint.json = JsonParser.toString(bluePrintJson);
            updateBluePrint(bluePrint);
            return true;
        }
        return false;
    }


    public boolean removeWidget(String email, String blueprint_id, long widgetId) {
        BluePrint bluePrint = bluePrints.get(blueprint_id);
        if (bluePrint != null && bluePrint.email.equals(email)) {
            BluePrintJson bluePrintJson = JsonParser.parse(BluePrintJson.class, bluePrint.json);
            if(!bluePrintJson.widgets.removeIf(w -> w.id == widgetId)){
                return false;
            }
            bluePrint.json = JsonParser.toString(bluePrintJson);
            updateBluePrint(bluePrint);
            return true;
        }
        return false;
    }

    public ArrayList<BluePrint> getAllBluePrints() {
        ArrayList<BluePrint> data = new ArrayList<>();
        for (BluePrint bluePrint : bluePrints.values()) {
            data.add(bluePrint);
        }
        return data;
    }

}
