package cz.lukaskabc.ontology.ontopus.plugin.webhook.persistence.converter;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;

import java.util.regex.Pattern;

@Converter
public class PatternConverter implements AttributeConverter<Pattern, String> {
    @Override
    public Pattern convertToAttribute(String value) {
        return Pattern.compile(value);
    }

    @Override
    public String convertToAxiomValue(Pattern value) {
        return value.pattern();
    }
}
