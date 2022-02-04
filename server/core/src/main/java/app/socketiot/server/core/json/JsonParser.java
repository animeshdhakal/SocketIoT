package app.socketiot.server.core.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import app.socketiot.server.core.json.model.BluePrintJson;

public class JsonParser {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));

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

    public static String toString(Object obj, String filterName, String... exceptFields) {
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptFields);
        FilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false).addFilter(filterName,
                filter);
        try {
            return mapper.writer(provider).writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
