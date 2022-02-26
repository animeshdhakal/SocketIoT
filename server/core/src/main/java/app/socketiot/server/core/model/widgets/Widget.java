package app.socketiot.server.core.model.widgets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Button.class, name = "BUTTON"),
        @JsonSubTypes.Type(value = Label.class, name = "LABEL")
})

public abstract class Widget {
    public int x = 0;
    public int y = 0;
    public int pin = -1;
    public int pinMode = -1;

    public Widget() {
    }

}
