package cz.lukaskabc.ontology.ontopus.core.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core.model.id.ArtifactURI;

@Converter(autoApply = true)
public class ArtifactUriConverter extends AbstractEntityIdentifierConverter<ArtifactURI>
        implements AttributeConverter<ArtifactURI, String> {
    @Override
    public ArtifactURI convertToAttribute(String value) {
        return new ArtifactURI(value);
    }
}
