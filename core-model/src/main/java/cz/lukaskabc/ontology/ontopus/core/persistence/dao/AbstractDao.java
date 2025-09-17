package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.lukaskabc.ontology.ontopus.core.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import java.net.URI;
import org.jspecify.annotations.Nullable;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.validation.Validator;

public abstract class AbstractDao<T extends PersistenceEntity> {
    protected final EntityManager em;
    protected final Class<T> entityClass;
    protected final URI typeUri;
    protected final Validator validator;
    protected final Descriptor descriptor;
    protected final URI entityGraphContext;

    public AbstractDao(
            Class<T> entityClass, URI typeUri, EntityManager em, Validator validator, Descriptor descriptor) {
        this.entityClass = entityClass;
        this.typeUri = typeUri;
        this.em = em;
        this.validator = validator;
        this.descriptor = descriptor;
        this.entityGraphContext = descriptor.getSingleContext().orElseThrow();
    }

    @Nullable public T find(URI uri) {
        try {
            return em.<@Nullable T>find(entityClass, uri, descriptor);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void merge(T entity) {
        em.merge(validated(entity), descriptor);
    }

    public void persist(T entity) {
        em.persist(validated(entity), descriptor);
    }

    @Nullable protected <E> E resultOrNull(ThrowingSupplier<E> supplier) {
        try {
            return supplier.get(PersistenceException::new);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Nullable protected T validated(@Nullable T entity) {
        if (entity != null) {
            validator.validateObject(entity).failOnError(ValidationException::new);
        }
        return entity;
    }
}
