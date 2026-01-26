package cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;

/**
 * Object capable of identifier generation for a specific entity
 *
 * @param <I> The entity identifier type
 * @param <E> The entity type
 */
public interface IdentifierGenerator<I extends EntityIdentifier, E extends PersistenceEntity<I>> {
    I generate(E entity);
}
