package app.socketiot.server.core.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Widget{
    public String type;
    public int x = -1;
    public int y = -1;
    public int width = -1;
    public int height = -1;
    public int pin = -1;
    public long id = -1;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String blueprint_id;


    public Widget(String type, int x, int y, int width, int height, int pin) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pin = pin;
    }

    public Widget() {
    }
}
