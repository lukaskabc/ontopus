package cz.lukaskabc.ontology.ontopus.core.persistence;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.validation.Validator;

import java.net.URI;

public abstract class BaseDao<T extends PersistenceEntity> {
    protected final Validator validator;
    protected final Class<T> entityClass;
    protected final URI typeUri;
    protected final EntityManager em;

    public BaseDao(Class<T> entityClass, URI typeUri, EntityManager em, Validator validator) {
        this.entityClass = entityClass;
        this.typeUri = typeUri;
        this.em = em;
        this.validator = validator;
    }

    @Nullable
    public T find(URI uri) {
        try {
            return em.<@Nullable T>find(entityClass, uri);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }


    public void persist(T entity) {
        em.persist(validated(entity));
    }

    public void merge(T entity) {
        em.merge(validated(entity));
    }

    @Nullable
    protected T validated(@Nullable T entity) {
        if (entity != null) {
            validator.validateObject(entity)
                    .failOnError(ValidationException::new);
        }
        return entity;
    }

    @Nullable
    protected <E> E handleExceptions(ThrowingSupplier<E> supplier) {
        try {
            return supplier.get(PersistenceException::new);
        } catch (NoResultException e) {
            return null;
        }
    }

}
