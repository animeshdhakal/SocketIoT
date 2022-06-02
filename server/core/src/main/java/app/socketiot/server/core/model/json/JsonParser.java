package app.socketiot.server.core.model.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;

public class JsonParser {
    private static final Logger log = LogManager.getLogger(JsonParser.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    private static final ObjectWriter objWriterPrivate = mapper.writerWithView(View.Private.class);
    private static final ObjectReader objReaderPrivate = mapper.readerWithView(View.Private.class);

    private static final ObjectWriter objWriterProtected = mapper.writerWithView(View.Protected.class);
    private static final ObjectReader objReaderProtected = mapper.readerWithView(View.Protected.class);

    public static String toJson(Object obj, ObjectWriter writer) {
        try {
            return writer.writeValueAsString(obj);
        } catch (Exception e) {
            log.debug("Unexpected Error Occured while converting object to json", e);
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz, ObjectReader reader) {
        try {
            return reader.readValue(json, clazz);
        } catch (Exception e) {
            log.debug("Unexpected Error Occured while converting json to object", e);
            return null;
        }
    }

    public static String toPrivateJson(Object obj) {
        return toJson(obj, objWriterPrivate);
    }

    public static String toProtectedJson(Object obj) {
        return toJson(obj, objWriterProtected);
    }

    public static <T> T parsePrivateJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, objReaderPrivate);
    }

    public static <T> T parseProtectedJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, objReaderProtected);
    }

}
