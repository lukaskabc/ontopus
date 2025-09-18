package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.lukaskabc.ontology.ontopus.core.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core.model.id.EntityIdentifier;
import cz.lukaskabc.ontology.ontopus.core.persistence.identifier.IdentifierGenerator;
import java.net.URI;
import org.jspecify.annotations.Nullable;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.validation.Validator;

public abstract class AbstractDao<I extends EntityIdentifier, E extends PersistenceEntity<I>> {
    protected final EntityManager em;
    protected final Class<E> entityClass;
    protected final URI typeUri;
    protected final Validator validator;
    protected final Descriptor descriptor;
    protected final URI entityGraphContext;
    protected final IdentifierGenerator<I, E> identifierGenerator;

    public <G extends IdentifierGenerator<I, E>> AbstractDao(
            Class<E> entityClass,
            URI typeUri,
            EntityManager em,
            Validator validator,
            Descriptor descriptor,
            G identifierGenerator) {
        this.entityClass = entityClass;
        this.typeUri = typeUri;
        this.em = em;
        this.validator = validator;
        this.descriptor = descriptor;
        this.entityGraphContext = descriptor.getSingleContext().orElseThrow();
        this.identifierGenerator = identifierGenerator;
    }

    @Nullable public E find(URI uri) {
        try {
            return em.<@Nullable E>find(entityClass, uri, descriptor);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void merge(E entity) {
        entity.setUri(entity.getIdentifier().toURI());
        em.merge(validated(entity), descriptor);
    }

    public void persist(E entity) {
        setIdentifierIfMissing(entity);
        entity.setUri(entity.getIdentifier().toURI());
        em.persist(validated(entity), descriptor);
    }

    @Nullable protected <T> T resultOrNull(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get(PersistenceException::new);
        } catch (NoResultException e) {
            return null;
        }
    }

    protected void setIdentifierIfMissing(E entity) {
        if (entity.getIdentifier() == null) {
            entity.setIdentifier(identifierGenerator.generate(entity));
        }
    }

    @Nullable protected <T> T validated(@Nullable T entity) {
        if (entity != null) {
            validator.validateObject(entity).failOnError(ValidationException::new);
        }
        return entity;
    }
}
