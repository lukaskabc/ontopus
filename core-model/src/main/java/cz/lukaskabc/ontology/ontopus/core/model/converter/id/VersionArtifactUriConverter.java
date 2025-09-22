package cz.lukaskabc.ontology.ontopus.core.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionArtifactURI;

@Converter(autoApply = true)
public class VersionArtifactUriConverter extends AbstractEntityIdentifierConverter<VersionArtifactURI>
        implements AttributeConverter<VersionArtifactURI, String> {
    @Override
    public VersionArtifactURI convertToAttribute(String value) {
        return new VersionArtifactURI(value);
    }
}
