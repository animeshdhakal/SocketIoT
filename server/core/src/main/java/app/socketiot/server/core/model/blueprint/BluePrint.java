package app.socketiot.server.core.model.blueprint;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonView;
import app.socketiot.server.core.json.View;
import app.socketiot.server.core.model.widgets.type.OnOffWidget;
import app.socketiot.server.core.model.widgets.type.Widget;
import app.socketiot.server.utils.NumberUtil;

public class BluePrint {
    public volatile String name;

    public volatile String id;

    @JsonView(View.Private.class)
    public List<Widget> widgets;

    public BluePrint(String name, String id, List<Widget> widgets) {
        this.name = name;
        this.id = id;
        this.widgets = widgets;
    }

    public BluePrint(String id) {
        this.id = id;
    }

    public BluePrint() {

    }

    public String getWidgetNameByPin(int pin) {
        for (Widget widget : widgets) {
            if (widget.pin == pin) {
                return widget.name;
            }
        }
        return null;
    }

    public OnOffWidget getOnOffWidgetByPin(String pin) {
        for (Widget widget : widgets) {
            if (widget.pin == NumberUtil.parsePin(pin) && widget instanceof OnOffWidget) {
                return (OnOffWidget) widget;
            }
        }
        return null;
    }
}
