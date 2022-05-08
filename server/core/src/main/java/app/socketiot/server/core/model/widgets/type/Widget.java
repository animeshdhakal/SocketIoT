package app.socketiot.server.core.model.widgets.type;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import app.socketiot.server.core.model.widgets.Button;
import app.socketiot.server.core.model.widgets.Label;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Button.class, name = "BUTTON"),
        @JsonSubTypes.Type(value = Label.class, name = "LABEL")
})

public abstract class Widget {
    public int x = 0;
    public int y = 0;
    public short pin = -1;
    public int pinMode = -1;
    public String name;

    public Widget() {
    }

}
