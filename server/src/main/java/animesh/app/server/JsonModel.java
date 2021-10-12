package animesh.app.server;

import java.util.HashMap;
import java.util.Map;

public class JsonModel {
    public Map<String, String> pins;

    static public JsonModel createJsonModelObj() {
        JsonModel mdl = new JsonModel();
        mdl.pins = new HashMap<>();
        return mdl;
    }
}
