package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import java.net.URI;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.util.function.ThrowingSupplier;

public abstract class AbstractDao<I extends EntityIdentifier, E extends PersistenceEntity<I>> {
    protected final EntityManager em;
    protected final Class<E> entityClass;
    protected final URI typeUri;
    protected final Descriptor descriptor;
    protected final URI entityGraphContext;

    public AbstractDao(Class<E> entityClass, URI typeUri, EntityManager em, Descriptor descriptor) {
        this.entityClass = entityClass;
        this.typeUri = typeUri;
        this.em = em;
        this.descriptor = descriptor;
        this.entityGraphContext = descriptor.getSingleContext().orElseThrow();
    }

    public void delete(E entity) {
        Objects.requireNonNull(entity.getIdentifier());
        try {
            em.createNativeQuery("""
					DELETE WHERE {
					    GRAPH ?context {
					        ?entity ?r ?o .
					    }
					}
					""")
                    .setParameter("context", entityGraphContext)
                    .setParameter("entity", entity.getIdentifier())
                    .executeUpdate();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public boolean exists(I identifier) {
        Objects.requireNonNull(identifier);
        try {
            return em.createNativeQuery("""
					ASK FROM ?context WHERE {
					    ?entity a ?type .
					}
					""", Boolean.class)
                    .setParameter("context", entityGraphContext)
                    .setParameter("type", typeUri)
                    .setDescriptor(descriptor)
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Nullable public E find(I identifier) {
        try {
            Objects.requireNonNull(identifier);
            return em.<@Nullable E>find(entityClass, identifier.toURI(), descriptor);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void merge(E entity) {
        Objects.requireNonNull(entity);
        em.merge(entity, descriptor);
    }

    public void persist(E entity) {
        Objects.requireNonNull(entity);
        em.persist(entity, descriptor);
    }

    @Nullable protected <T> T resultOrNull(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get(PersistenceException::new);
        } catch (NoResultException e) {
            return null;
        }
    }
}
