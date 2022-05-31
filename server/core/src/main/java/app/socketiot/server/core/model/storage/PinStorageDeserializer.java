package app.socketiot.server.core.model.storage;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PinStorageDeserializer extends JsonDeserializer<PinStorage> {
    public static final Logger log = LogManager.getLogger(PinStorageDeserializer.class);

    @Override
    public PinStorage deserialize(JsonParser jp, DeserializationContext ctxt) {
        try {
            JsonToken token = jp.getCurrentToken();

            if (token == JsonToken.VALUE_STRING) {
                return new SingleValuePinStorage(jp.getValueAsString());
            }

            if (token == JsonToken.START_OBJECT) {
                JsonNode node = jp.getCodec().readTree(jp);
                JsonNode type = node.get("type");

                if (type != null) {
                    MultiValuePinStorage multiValuePinStorage = new MultiValuePinStorage(
                            MultiValuePinStorageType.valueOf(type.asText()));

                    JsonNode values = node.get("values");

                    if (values != null && values.isArray()) {
                        for (JsonNode value : values) {
                            multiValuePinStorage.values.add(value.asText());
                        }
                    }

                    return multiValuePinStorage;
                }
            }

        } catch (Exception e) {
            log.error("Error deserializing pin storage", e);
        }

        return null;
    }
}
