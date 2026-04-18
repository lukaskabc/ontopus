package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;

/** A persistence (JOPA) entity with an identifier */
public interface PersistenceEntity<ID extends TypedIdentifier> {
    ID getIdentifier();

    void setIdentifier(ID identifier);
}
