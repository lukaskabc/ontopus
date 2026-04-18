package cz.lukaskabc.ontology.ontopus.core_model.service.base;

import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@NullMarked
public abstract class BaseService<
        I extends TypedIdentifier, E extends PersistenceEntity<I>, R extends AbstractRepository<I, E, ?>> {
    private static final Logger log = LogManager.getLogger(BaseService.class);
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

    @Transactional
    public void persist(E entity) {
        if (repository.exists(entity.getIdentifier())) {
            log.throwing(PersistenceException.builder()
                    .internalMessage("Entity with identifier already exists")
                    .detailMessageArguments(new Object[] {entity.getIdentifier()})
                    .detailMessageCode("ontopus.core.error.entityExists")
                    .titleMessageCode("ontopus.core.error.unableToPersist")
                    .build());
        }
        repository.persist(entity);
    }

    @Transactional
    public void save(E entity) {
        if (entity.getIdentifier() == null) {
            persist(entity);
        } else {
            update(entity);
        }
    }

    public void update(E entity) {
        repository.update(entity);
    }
}
