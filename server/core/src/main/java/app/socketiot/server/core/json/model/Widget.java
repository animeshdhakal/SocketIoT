package app.socketiot.server.core.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Button.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Button.class, name = "button"),
})

public abstract class Widget {
    public int x = 0;
    public int y = 0;
    public int width = -1;
    public int height = -1;
    public int pin = -1;
    public long id = -1;
    // Pin Mode is 1 if the pin is an input, 0 if it is an output
    public int pinMode = -1;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String blueprint_id;

    public Widget() {
    }

}
