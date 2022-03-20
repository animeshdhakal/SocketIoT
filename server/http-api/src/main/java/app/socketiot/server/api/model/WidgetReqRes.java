package app.socketiot.server.api.model;

import java.util.List;

import app.socketiot.server.core.model.widgets.Widget;

public class WidgetReqRes {
    public String blueprint_id;
    public List<Widget> widgets;

    public WidgetReqRes(List<Widget> widgets, String blueprint_id) {
        this.widgets = widgets;
        this.blueprint_id = blueprint_id;
    }

    public WidgetReqRes() {
    }
}
