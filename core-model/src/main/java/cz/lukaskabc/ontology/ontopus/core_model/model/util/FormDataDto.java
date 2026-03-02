package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FormDataDto extends HashMap<String, String> implements Serializable {
    public FormDataDto() {}

    public FormDataDto(Map<? extends String, ? extends String> m) {
        super(m);
    }

    public FormDataDto(int initialCapacity) {
        super(initialCapacity);
    }

    public FormDataDto(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
}
