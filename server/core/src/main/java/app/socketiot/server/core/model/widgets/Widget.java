package app.socketiot.server.core.model.widgets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import app.socketiot.server.core.model.widgets.ui.input.Button;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Button.class, name = "BUTTON"),
})
public abstract class Widget {
    public long x;

    public long y;

    public volatile String name;

    public short pin = -1;
}
