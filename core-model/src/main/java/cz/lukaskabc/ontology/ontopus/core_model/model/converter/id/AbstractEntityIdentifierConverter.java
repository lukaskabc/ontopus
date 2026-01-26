package cz.lukaskabc.ontology.ontopus.core_model.model.converter.id;

import cz.cvut.kbss.jopa.model.AttributeConverter;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;

public abstract class AbstractEntityIdentifierConverter<ID extends EntityIdentifier>
        implements AttributeConverter<ID, String> {
    @Override
    public String convertToAxiomValue(ID value) {
        return value.toString();
    }
}
