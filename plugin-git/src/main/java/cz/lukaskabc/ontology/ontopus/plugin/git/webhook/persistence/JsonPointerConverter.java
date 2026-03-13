package cz.lukaskabc.ontology.ontopus.plugin.git.webhook.persistence;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import tools.jackson.core.JsonPointer;

@Converter(autoApply = true)
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
