package cz.lukaskabc.ontology.ontopus.api.model;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

public class FormJsonDataDto extends HashMap<String, JsonNode> {
    public static ObjectNode formDataAsObjectNode(ObjectMapper objectMapper, Map<String, JsonNode> formData) {
        final ObjectNode result = objectMapper.createObjectNode();
        for (Map.Entry<String, JsonNode> entry : formData.entrySet()) {
            result.set(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public FormJsonDataDto() {}

    public FormJsonDataDto(Map<? extends String, ? extends JsonNode> m) {
        super(m);
    }

    public FormJsonDataDto(int initialCapacity) {
        super(initialCapacity);
    }

    public FormJsonDataDto(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ObjectNode asObjectNode(ObjectMapper objectMapper) {
        return formDataAsObjectNode(objectMapper, this);
    }
}
