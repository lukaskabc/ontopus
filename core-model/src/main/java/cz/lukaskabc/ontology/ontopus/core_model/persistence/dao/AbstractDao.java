package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.function.ThrowingSupplier;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private String buildOrderByClause(Pageable pageable) {
        String orderBy = "ORDER BY ?entity";
        if (pageable.getSort().isSorted()) {
            // TODO use metamodel to build the order by clause
            // StringBuilder sb = new StringBuilder("ORDER BY ");
            // pageable.getSort().forEach(order -> {
            // String direction = order.isAscending() ? "ASC" : "DESC";
            // sb.append(direction).append("(?").append(order.getProperty()).append(") ");
            // });
            // orderBy = sb.toString();
        }
        return orderBy;
    }

    public void delete(E entity) {
        Objects.requireNonNull(entity.getIdentifier());
        try {
            Optional.ofNullable(find(entity.getIdentifier())).ifPresent(em::remove);
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

    @Transactional
    public Page<E> find(Pageable pageable) {
        Objects.requireNonNull(pageable);
        try {
            String orderBy = buildOrderByClause(pageable);
            String query = """
					SELECT DISTINCT ?entity FROM ?context WHERE {
					    ?entity a ?type .
					    OPTIONAL { ?entity ?p ?o . }
					}
					%s
					LIMIT %d
					OFFSET %d
					""".formatted(orderBy, pageable.getPageSize(), pageable.getOffset());

            List<E> results = em.createNativeQuery(query, entityClass)
                    .setParameter("context", entityGraphContext)
                    .setParameter("type", typeUri)
                    .getResultList();

            // 3. Fetch the total count
            long total = em.createNativeQuery("""
					SELECT (COUNT(DISTINCT ?entity) AS ?count) FROM ?context WHERE {
					    ?entity a ?type .
					}
					""", Long.class)
                    .setParameter("context", entityGraphContext)
                    .setParameter("type", typeUri)
                    .getSingleResult();

            return new PageImpl<>(results, pageable, total);

        } catch (RuntimeException e) {
            throw new PersistenceException("Could not retrieve paginated and sorted entities", e);
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
