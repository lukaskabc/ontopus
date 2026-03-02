package cz.lukaskabc.ontology.ontopus.core_model.service.base;

import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@NullMarked
public abstract class BaseService<
        I extends TypedIdentifier, E extends PersistenceEntity<I>, R extends AbstractRepository<I, E, ?>> {
    protected final R repository;

    public BaseService(R repository) {
        this.repository = repository;
    }

    public void delete(E entity) {
        repository.delete(entity);
    }

    public void deleteById(I id) {
        repository.deleteById(id);
    }

    public Page<E> find(Pageable pageable, List<String> filter) {
        return repository.find(pageable, filter);
    }

    public Optional<E> findById(@Nullable I id) {
        return Optional.ofNullable(repository.find(id));
    }

    public E findRequiredById(I id) {
        return repository.findRequired(id);
    }

    public void update(E entity) {
        repository.update(entity);
    }
}
