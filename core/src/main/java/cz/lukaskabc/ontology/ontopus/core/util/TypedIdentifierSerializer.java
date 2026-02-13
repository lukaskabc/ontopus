package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class TypedIdentifierSerializer extends ValueSerializer<TypedIdentifier> {
    @Override
    public void serialize(TypedIdentifier value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writePOJO(value.toURI());
        }
    }
}
