package cz.lukaskabc.ontology.ontopus.core.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;

@Converter(autoApply = true)
public class TemporaryContextUriConverter extends AbstractEntityIdentifierConverter<TemporaryContextURI>
        implements AttributeConverter<TemporaryContextURI, String> {
    @Override
    public TemporaryContextURI convertToAttribute(String value) {
        return new TemporaryContextURI(value);
    }
}
