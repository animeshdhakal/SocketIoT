package app.socketiot.server.core.json.model;

import java.util.List;

public class BluePrintJson {
    public List<Widget> widgets;

    public BluePrintJson(List<Widget> widgets) {
        this.widgets = widgets;
    }

    public BluePrintJson() {
    }
}
