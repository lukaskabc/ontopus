package cz.lukaskabc.ontology.ontopus.core_model.service.base;

import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractEntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;

import java.util.Optional;

public abstract class BaseEntityService<
        I extends AbstractEntityIdentifier, E extends PersistenceEntity<I>, R extends AbstractRepository<I, E, ?>> {
    protected final R repository;

    public BaseEntityService(R repository) {
        this.repository = repository;
    }

    public Optional<E> findById(I id) {
        return Optional.ofNullable(repository.find(id));
    }
}
