package app.socketiot.server.core.json.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Button.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Button.class, name = "button"),
})

public abstract class Widget {
    public int x = 0;
    public int y = 0;
    public int pin = -1;
    public int pinMode = -1;

    public Widget() {
    }

}
