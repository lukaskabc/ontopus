package cz.lukaskabc.ontology.ontopus.core.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.model.id.EntityIdentifier;

/**
 * Object capable of identifier generation for a specific entity
 *
 * @param <I> The entity identifier type
 * @param <E> The entity type
 */
public interface IdentifierGenerator<I extends EntityIdentifier, E extends PersistenceEntity<I>> {
    I generate(E entity);
}
