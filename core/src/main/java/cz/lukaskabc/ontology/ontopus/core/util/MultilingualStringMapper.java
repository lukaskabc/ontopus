package cz.lukaskabc.ontology.ontopus.core.util;

import cz.cvut.kbss.jopa.model.MultilingualString;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class MultilingualStringMapper extends ValueSerializer<MultilingualString> {

    @Override
    public void serialize(MultilingualString value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {
        if (value != null && value.getValue() != null) {
            gen.writePOJO(value.getValue());
        } else {
            gen.writeNull();
        }
    }
}
