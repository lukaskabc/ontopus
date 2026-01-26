package cz.lukaskabc.ontology.ontopus.core_model.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.cvut.kbss.jopa.model.annotations.Converter;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;

@Converter(autoApply = true)
public class OntopusCatalogUriConverter extends AbstractEntityIdentifierConverter<OntopusCatalogURI>
        implements AttributeConverter<OntopusCatalogURI, String> {

    @Override
    public OntopusCatalogURI convertToAttribute(String value) {
        return new OntopusCatalogURI(value);
    }
}
