package app.socketiot.server.core.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonParser {
    public static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .setSerializationInclusion(Include.NON_NULL);

    private static final ObjectReader limitedObjectReader = mapper.readerWithView(View.Public.class);
    private static final ObjectWriter limitedObjectWriter = mapper.writerWithView(View.Public.class);

    private static final ObjectWriter objWriter = mapper.writer();
    private static final ObjectReader objReader = mapper.reader();

    public static String toJson(Object obj) {
        try {
            return objWriter.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T parse(Class<T> clazz, String json) {
        try {
            return objReader.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toLimitedJson(Object obj) {
        try {
            return limitedObjectWriter.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T parseLimitedJson(Class<T> clazz, String json) {
        try {
            return limitedObjectReader.readValue(json, clazz);
        } catch (Exception e) {  
            return null;
        }
    }

}
