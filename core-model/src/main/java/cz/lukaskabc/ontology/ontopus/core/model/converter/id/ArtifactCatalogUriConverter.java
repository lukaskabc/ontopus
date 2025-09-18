package cz.lukaskabc.ontology.ontopus.core.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core.model.id.ArtifactCatalogURI;

@Converter(autoApply = true)
public class ArtifactCatalogUriConverter extends AbstractEntityIdentifierConverter<ArtifactCatalogURI>
        implements AttributeConverter<ArtifactCatalogURI, String> {

    @Override
    public ArtifactCatalogURI convertToAttribute(String value) {
        return new ArtifactCatalogURI(value);
    }
}
