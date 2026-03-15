package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.converter;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import tools.jackson.core.JsonPointer;

@Converter
public class JsonPointerConverter implements AttributeConverter<JsonPointer, String> {
    @Override
    public JsonPointer convertToAttribute(String value) {
        return JsonPointer.compile(value);
    }

    @Override
    public String convertToAxiomValue(JsonPointer value) {
        return value.toString();
    }
}
