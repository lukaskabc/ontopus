package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base;

import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Contract;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractRepository<
        I extends TypedIdentifier, E extends PersistenceEntity<I>, D extends AbstractDao<I, E>> {
    protected final D dao;
    protected final Validator validator;

    @Nullable protected final IdentifierGenerator<I, E> identifierGenerator;

    public AbstractRepository(D dao, Validator validator, @Nullable IdentifierGenerator<I, E> identifierGenerator) {
        this.dao = dao;
        this.validator = validator;
        this.identifierGenerator = identifierGenerator;
    }

    /** @see #dao#delete(PersistenceEntity) */
    @Transactional
    public void delete(E entity) {
        Objects.requireNonNull(entity.getIdentifier(), "Entity identifier must not be null");
        dao.delete(entity.getIdentifier());
    }

    @Transactional
    public void deleteById(I id) {
        dao.delete(id);
    }

    /**
     * Checks if an entity with the given identifier exists.
     *
     * @param identifier the identifier to check for existence
     * @return true if an entity with the given identifier exists, false otherwise
     * @see #dao#exists(Object)
     */
    @Transactional(readOnly = true)
    public boolean exists(@Nullable I identifier) {
        if (identifier == null) {
            return false;
        }
        return dao.exists(identifier);
    }

    /**
     * Finds an entity by its identifier.
     *
     * @param identifier the identifier of the entity to find
     * @return the entity with the given identifier, or null if no such entity exists
     */
    @Transactional(readOnly = true)
    @Nullable public E find(@Nullable I identifier) {
        if (identifier == null) {
            return null;
        }
        return dao.find(identifier);
    }

    /**
     * Finds entities matching the given filter and pagination parameters.
     *
     * @param pageable the pagination parameters
     * @param filter the filter criteria
     * @return a page of entities matching the given filter and pagination parameters
     * @see #dao#find(Pageable, List)
     */
    @Transactional(readOnly = true)
    public Page<E> find(Pageable pageable, List<String> filter) {
        if (pageable.isUnpaged()) {
            pageable = PageRequest.of(0, 100); // TODO config
        }
        List<E> content = dao.find(pageable, filter);
        long totalCount = dao.count(filter);
        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * Finds an entity by its identifier, throwing an exception if no such entity exists.
     *
     * @param identifier the identifier of the entity to find
     * @return the entity with the given identifier
     */
    @Transactional(readOnly = true)
    public E findRequired(I identifier) {
        Objects.requireNonNull(identifier);
        return Optional.ofNullable(find(identifier)).orElseThrow(() -> notFound(identifier));
    }

    protected NotFoundException notFound(I identifier) {
        return new NotFoundException(
                "Entity of type <" + dao.getTypeUri() + "> with identifier <" + identifier + "> not found");
    }

    /**
     * Persists a new entity, setting its identifier if it is missing.
     *
     * @param entity the entity to persist
     */
    @Transactional
    public void persist(E entity) {
        Objects.requireNonNull(entity);
        setIdentifierIfMissing(entity);
        dao.persist(validated(entity));
    }

    /** Sets the identifier of the given entity if it is missing. */
    protected void setIdentifierIfMissing(E entity) {
        if (entity.getIdentifier() == null && identifierGenerator != null) {
            final I identifier = identifierGenerator.generate(entity);
            if (identifier == null) {
                throw new IllegalStateException(
                        "Identifier generator returned null for entity of type <" + dao.getTypeUri() + ">");
            }
            entity.setIdentifier(identifier);
        }
    }

    /**
     * Merges the given entity. The entity and its identifier must not be null.
     *
     * @param entity the entity to merge
     */
    @Transactional
    public void update(E entity) {
        Objects.requireNonNull(entity, "Entity must not be null");
        Objects.requireNonNull(entity.getIdentifier(), "Entity identifier must not be null");
        dao.merge(validated(entity));
    }

    /**
     * Validates the given entity using the configured validator.
     *
     * @param entity the entity to validate
     * @return the validated entity
     * @param <T> the type of the entity
     * @throws ValidationException if the entity is invalid
     */
    @Contract("_ -> param1")
    protected <T> T validated(T entity) {
        BeanPropertyBindingResult errors =
                new BeanPropertyBindingResult(entity, entity.getClass().getSimpleName());
        validator.validate(entity, errors);
        errors.failOnError(ValidationException::new);
        return entity;
    }
}
