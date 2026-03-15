package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.converter;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;

import java.util.regex.Pattern;

@Converter(autoApply = true)
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
