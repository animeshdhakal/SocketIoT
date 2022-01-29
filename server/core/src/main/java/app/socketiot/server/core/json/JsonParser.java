package app.socketiot.server.core.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.socketiot.server.core.json.model.BluePrintJson;

public class JsonParser {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static BluePrintJson parseBluePrintJson(String json) {
        try {
            return mapper.readValue(json, BluePrintJson.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static <T> T parse(Class<T> clazz, String json) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
