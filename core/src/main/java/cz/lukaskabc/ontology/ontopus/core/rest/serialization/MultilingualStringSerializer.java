package cz.lukaskabc.ontology.ontopus.core.rest.serialization;

import static cz.lukaskabc.ontology.ontopus.core.rest.serialization.MultilingualStringDeserializer.NO_LANGUAGE_KEY;

import cz.cvut.kbss.jopa.model.MultilingualString;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.Map;

public class MultilingualStringSerializer extends ValueSerializer<MultilingualString> {

    private void serialize(Map<String, String> nullableKeyMap, JsonGenerator gen) {
        gen.writeStartObject();
        nullableKeyMap.forEach((key, value) -> {
            if (key == null) {
                key = NO_LANGUAGE_KEY;
            }
            gen.writeName(key);
            gen.writeString(value);
        });
        gen.writeEndObject();
    }

    @Override
    public void serialize(MultilingualString value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {
        if (value != null && value.getValue() != null) {
            serialize(value.getValue(), gen);
        } else {
            gen.writeNull();
        }
    }
}
