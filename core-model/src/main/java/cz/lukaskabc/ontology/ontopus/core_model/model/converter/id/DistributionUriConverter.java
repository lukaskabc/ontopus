package cz.lukaskabc.ontology.ontopus.core_model.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;

@Converter(autoApply = true)
public class DistributionUriConverter extends AbstractEntityIdentifierConverter<DistributionURI>
        implements AttributeConverter<DistributionURI, String> {
    @Override
    public DistributionURI convertToAttribute(String value) {
        return new DistributionURI(value);
    }
}
