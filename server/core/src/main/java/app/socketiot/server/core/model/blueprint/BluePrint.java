package app.socketiot.server.core.model.blueprint;

import app.socketiot.server.core.model.widgets.Widget;

public class BluePrint {
    public volatile String name;

    public volatile String id;

    public volatile Widget widgets[] = {};

    public BluePrint(String name, String id, Widget[] widgets) {
        this.name = name;
        this.id = id;
        this.widgets = widgets;
    }

    public boolean isInvalid() {
        return name == null || name.length() > 40;
    }

    public BluePrint() {
    }
}
