package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.AbstractDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractRepository<
        I extends EntityIdentifier, E extends PersistenceEntity<I>, D extends AbstractDao<I, E>> {
    protected final D dao;
    protected final Validator validator;
    protected final IdentifierGenerator<I, E> identifierGenerator;

    public AbstractRepository(D dao, Validator validator, IdentifierGenerator<I, E> identifierGenerator) {
        this.dao = dao;
        this.validator = validator;
        this.identifierGenerator = identifierGenerator;
    }

    @Transactional
    public void delete(E entity) {
        dao.delete(entity);
    }

    @Transactional
    public boolean exists(@Nullable I identifier) {
        if (identifier == null) {
            return false;
        }
        return dao.exists(identifier);
    }

    @Transactional
    @Nullable public E find(@Nullable I identifier) {
        if (identifier == null) {
            return null;
        }
        return dao.find(identifier);
    }

    @Transactional
    public Page<E> find(Pageable pageable) {
        if (pageable.isUnpaged()) {
            pageable = PageRequest.of(0, 100); // TODO config
        }
        return dao.find(pageable);
    }

    @Transactional
    public E findRequired(I identifier) {
        Objects.requireNonNull(identifier);
        return Optional.ofNullable(find(identifier)).orElseThrow();
    }

    @Transactional
    public void persist(E entity) {
        Objects.requireNonNull(entity);
        setIdentifierIfMissing(entity);
        dao.persist(validated(entity));
    }

    protected void setIdentifierIfMissing(E entity) {
        if (entity.getIdentifier() == null) {
            entity.setIdentifier(identifierGenerator.generate(entity));
        }
    }

    @Transactional
    public void update(E entity) {
        Objects.requireNonNull(entity);
        setIdentifierIfMissing(entity);
        dao.merge(validated(entity));
    }

    protected <T> T validated(T entity) {
        BeanPropertyBindingResult errors =
                new BeanPropertyBindingResult(entity, entity.getClass().getSimpleName());
        validator.validate(entity, errors);
        errors.failOnError(ValidationException::new);
        return entity;
    }
}
