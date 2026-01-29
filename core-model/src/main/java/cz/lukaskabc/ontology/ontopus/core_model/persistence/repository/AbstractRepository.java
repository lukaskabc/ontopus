package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.AbstractDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.IdentifierGenerator;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.validation.Validator;

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

    public boolean exists(@Nullable I identifier) {
        if (identifier == null) {
            return false;
        }
        return dao.exists(identifier);
    }

    @Nullable public E find(@Nullable I identifier) {
        if (identifier == null) {
            return null;
        }
        return dao.find(identifier);
    }

    public E findRequired(I identifier) {
        Objects.requireNonNull(identifier);
        return Optional.ofNullable(find(identifier)).orElseThrow();
    }

    public void persist(E entity) {
        Objects.requireNonNull(entity);
        setIdentifierIfMissing(entity);
        entity.setUri(entity.getIdentifier().toURI());
        dao.persist(validated(entity));
    }

    protected void setIdentifierIfMissing(E entity) {
        if (entity.getIdentifier() == null) {
            entity.setIdentifier(identifierGenerator.generate(entity));
        }
    }

    public void update(E entity) {
        Objects.requireNonNull(entity);
        setIdentifierIfMissing(entity);
        entity.setUri(entity.getIdentifier().toURI());
        dao.merge(validated(entity));
    }

    protected <T> T validated(T entity) {
        validator.validateObject(entity).failOnError(ValidationException::new);
        return entity;
    }
}
