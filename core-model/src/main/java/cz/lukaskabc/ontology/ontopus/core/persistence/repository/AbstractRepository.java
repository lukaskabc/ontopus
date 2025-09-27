package cz.lukaskabc.ontology.ontopus.core.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.AbstractDao;
import cz.lukaskabc.ontology.ontopus.core.persistence.identifier.IdentifierGenerator;
import java.util.Objects;
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

    @Nullable public E find(@Nullable I identifier) {
        if (identifier == null) {
            return null;
        }
        return dao.find(identifier);
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
