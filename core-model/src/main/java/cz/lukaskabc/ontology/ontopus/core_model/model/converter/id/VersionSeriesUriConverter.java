package cz.lukaskabc.ontology.ontopus.core_model.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;

@Converter(autoApply = true)
public class VersionSeriesUriConverter extends AbstractEntityIdentifierConverter<VersionSeriesURI>
        implements AttributeConverter<VersionSeriesURI, String> {
    @Override
    public VersionSeriesURI convertToAttribute(String value) {
        return new VersionSeriesURI(value);
    }
}
