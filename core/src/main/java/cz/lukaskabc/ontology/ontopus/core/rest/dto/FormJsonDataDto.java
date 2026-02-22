package cz.lukaskabc.ontology.ontopus.core.rest.dto;

import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class FormJsonDataDto extends HashMap<String, JsonNode> {
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
}
