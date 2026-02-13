package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import cz.cvut.kbss.jopa.model.query.Query;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.apache.commons.lang3.stream.Streams;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.function.ThrowingSupplier;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractDao<I extends TypedIdentifier, E extends PersistenceEntity<I>> {
    protected static String buildFilterClause(List<String> filter) {
        if (filter == null || filter.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("?entity ?anyPredicate ?anyValue . ");
        sb.append('\n');
        for (int i = 0; i < filter.size(); i++) {
            sb.append("FILTER(CONTAINS(LCASE(STR(?anyValue)), LCASE(?filterValue%d))) .\n".formatted(i));
        }
        return sb.toString();
    }

    protected static String buildOrderClause(Map<Attribute<?, ?>, String> orders) {
        return "ORDER BY %s ASC(?entity)".formatted(String.join(" ", orders.values()));
    }

    protected static void setFilterParams(Query query, List<String> filter) {
        if (filter == null) {
            return;
        }
        for (int i = 0; i < filter.size(); i++) {
            query.setParameter("filterValue%d".formatted(i), filter.get(i));
        }
    }

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

    protected String buildLimitOffsetClause(Pageable pageable) {
        if (pageable.isPaged()) {
            return "LIMIT %d OFFSET %d".formatted(pageable.getPageSize(), pageable.getOffset());
        }
        return "";
    }

    protected String buildOptionalClause(Iterable<Attribute<?, ?>> attributes) {
        return Streams.of(attributes)
                .map(attr -> "OPTIONAL { ?entity ?%sType ?%s . }".formatted(attr.getName(), attr.getName()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Builds a map of attributes to their corresponding order expressions based on the provided pageable's sort
     * information.
     *
     * @param pageable the pageable containing sorting information
     * @return a map where keys are attributes and values are their corresponding order expressions (e.g.,
     *     "ASC(attributeName)" or "DESC(attributeName)")
     */
    protected Map<Attribute<?, ?>, String> buildOrders(Pageable pageable) {
        final Map<Attribute<?, ?>, String> orders = new HashMap<>();
        EntityType<?> meta = em.getMetamodel().entity(entityClass);
        pageable.getSort().forEach(order -> {
            final String wrap =
                    switch (order.getDirection()) {
                        case ASC, DESC -> order.getDirection().name();
                        default ->
                            throw new IllegalArgumentException("Unsupported sort direction: " + order.getDirection());
                    };

            final Attribute<?, ?> attribute = meta.getAttribute(order.getProperty());
            Objects.requireNonNull(attribute);

            final String expression = "%s(?%s)".formatted(wrap, attribute.getName());
            orders.put(attribute, expression);
        });
        return orders;
    }

    protected String buildSelectParams(Iterable<Attribute<?, ?>> attributes) {
        return Streams.of(attributes)
                .map(Attribute::getName)
                .map(name -> "?" + name)
                .collect(Collectors.joining(" "));
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
    public Page<E> find(Pageable pageable, List<String> filter) {
        Objects.requireNonNull(pageable);
        assert pageable.isPaged();
        try {
            Map<Attribute<?, ?>, String> orders = buildOrders(pageable);
            final String orderClause = buildOrderClause(orders);
            final String limitOffsetClause = buildLimitOffsetClause(pageable);
            final String searchClause = buildFilterClause(filter);

            String query = """
					SELECT DISTINCT ?entity %s FROM ?context WHERE {
					    ?entity a ?type .
					    %s
					    %s
					}
					%s
					%s
					""".formatted(
                            buildSelectParams(orders.keySet()),
                            buildOptionalClause(orders.keySet()),
                            searchClause,
                            orderClause,
                            limitOffsetClause);

            TypedQuery<E> typedQuery = em.createNativeQuery(query, entityClass)
                    .setParameter("context", entityGraphContext)
                    .setParameter("type", typeUri)
                    .setDescriptor(descriptor);
            setTypeParams(typedQuery, orders.keySet());
            setFilterParams(typedQuery, filter);

            long total = em.createNativeQuery("""
					SELECT (COUNT(DISTINCT ?entity) AS ?count) FROM ?context WHERE {
					    ?entity a ?type .
					}
					""", Long.class)
                    .setParameter("context", entityGraphContext)
                    .setParameter("type", typeUri)
                    .getSingleResult();

            return new PageImpl<>(typedQuery.getResultList(), pageable, total);

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

    protected void setTypeParams(Query query, Iterable<Attribute<?, ?>> attributes) {
        for (Attribute<?, ?> attribute : attributes) {
            query.setParameter(attribute.getName() + "Type", attribute.getIRI());
        }
    }
}
