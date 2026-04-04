package cz.lukaskabc.ontology.ontopus.api.util;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

public class JsonUtils {
    public static ObjectNode getOrPutObject(JsonNode node, String key) {
        if (node.hasNonNull(key)) {
            return node.get(key).asObject();
        }
        return node.asObject().putObject(key);
    }

    private JsonUtils() {
        throw new AssertionError();
    }
}
