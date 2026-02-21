package cz.lukaskabc.ontology.ontopus.core_model.service.base;

import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public abstract class BaseEntityService<
        I extends EntityIdentifier, E extends PersistenceEntity<I>, R extends AbstractRepository<I, E, ?>> {
    protected final R repository;

    public BaseEntityService(R repository) {
        this.repository = repository;
    }

    public Optional<E> findById(@Nullable I id) {
        return Optional.ofNullable(repository.find(id));
    }
}
