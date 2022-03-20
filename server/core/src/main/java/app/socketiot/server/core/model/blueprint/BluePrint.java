package app.socketiot.server.core.model.blueprint;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonView;
import app.socketiot.server.core.json.View;
import app.socketiot.server.core.model.widgets.Widget;

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
}
