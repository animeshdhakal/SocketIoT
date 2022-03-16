package app.socketiot.server.core.json.model;

import java.util.List;

import app.socketiot.server.core.model.widgets.Widget;

public class BluePrintJson {
    public volatile List<Widget> widgets;

    public BluePrintJson(List<Widget> widgets) {
        this.widgets = widgets;
    }

    public BluePrintJson() {
    }
}
