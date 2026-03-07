package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.IRI;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import cz.cvut.kbss.jopa.model.query.Query;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.apache.commons.lang3.stream.Streams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.util.function.ThrowingSupplier;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractDao<I extends TypedIdentifier, E extends PersistenceEntity<I>> {
    private static final Logger LOG = LogManager.getLogger(AbstractDao.class);

    protected static String buildFilterClause(@Nullable List<String> filter) {
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

    public static PersistenceException persistenceException(Logger log, String message, Throwable cause) {
        log.error(message, cause);
        return new PersistenceException(message, cause);
    }

    /**
     * Executes the given supplier and returns its result. If a {@link NoResultException} is thrown during the
     * execution, null is returned instead.
     *
     * @param supplier the supplier to be executed, which may throw a {@link NoResultException} if no result is found
     * @return the result of the supplier if it executes successfully, or null if a {@link NoResultException} is thrown
     * @param <T> the type of the result returned by the supplier
     */
    @Nullable public static <T> T resultOrNull(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get(PersistenceException::new);
        } catch (NoResultException e) {
            return null;
        }
    }

    protected static void setFilterParams(Query query, @Nullable List<String> filter) {
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

    public AbstractDao(Class<E> entityClass, IRI typeUri, EntityManager em, Descriptor descriptor) {
        this.entityClass = entityClass;
        this.typeUri = typeUri.toURI();
        this.em = em;
        this.descriptor = descriptor;
        this.entityGraphContext = descriptor.getSingleContext().orElseThrow();
    }

    /**
     * Builds {@code LIMIT X OFFSET Y} clause for SPARQL query based on the provided pageable. If the pageable is not
     * paged, returns an empty string.
     *
     * @param pageable the pageable containing pagination information
     * @return a string representing the LIMIT and OFFSET clause for SPARQL query, or an empty string if pageable is not
     *     paged
     */
    protected String buildLimitOffsetClause(Pageable pageable) {
        if (pageable.isPaged()) {
            return "LIMIT %d OFFSET %d".formatted(pageable.getPageSize(), pageable.getOffset());
        }
        return "";
    }

    /**
     * Builds a series of OPTIONAL clauses for the given attributes to be included in the SPARQL query. One OPTIONAL
     * clause is generated for each attribute.
     *
     * @param attributes the attributes for which to build OPTIONAL clauses
     * @return a string containing the OPTIONAL clauses for the provided attributes
     */
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

    /**
     * Builds a string of parameters used for SELECT clause in SPARQL query. For each attribute, a parameter in the form
     * of "?attributeName" is created and all parameters are joined with a space.
     *
     * @param attributes the attributes for which to build SELECT parameters
     * @return a string of parameters for SELECT clause in SPARQL query, e.g. "?name ?age ?email"
     */
    protected String buildSelectParams(Iterable<Attribute<?, ?>> attributes) {
        return Streams.of(attributes)
                .map(Attribute::getName)
                .map(name -> "?" + name)
                .collect(Collectors.joining(" "));
    }

    /**
     * Counts the number of entities matching the provided filter criteria.
     *
     * @param filter a list of strings used for filtering entities based on their properties.
     * @return the count of entities matching the filter criteria
     * @see #find(Pageable, List) for details on how the filter is applied to the entities
     */
    public long count(List<String> filter) {
        return this.count(filter, "", q -> {});
    }

    /**
     * Counts the number of entities matching the provided filter criteria.
     *
     * @param filter a list of strings used for filtering entities based on their properties.
     * @return the count of entities matching the filter criteria
     * @see #find(Pageable, List) for details on how the filter is applied to the entities
     */
    public long count(List<String> filter, String additionalWhereClause, Consumer<TypedQuery<Long>> queryCustomizer) {
        try {
            final String searchClause = buildFilterClause(filter);
            String query = """
					SELECT (COUNT(DISTINCT ?entity) AS ?count) FROM ?context WHERE {
					    ?entity a ?type .
					    %s
					    %s
					}
					""".formatted(additionalWhereClause, searchClause);

            TypedQuery<Long> typedQuery = em.createNativeQuery(query, Long.class)
                    .setMaxResults(1)
                    .setParameter("context", entityGraphContext)
                    .setParameter("type", typeUri)
                    .setDescriptor(descriptor);
            setFilterParams(typedQuery, filter);
            queryCustomizer.accept(typedQuery);

            return typedQuery.getSingleResult();
        } catch (RuntimeException e) {
            throw persistenceException(LOG, "Could not count entities matching the filter criteria", e);
        }
    }

    /**
     * Deletes the given entity from its context. All triples with the entity as a subject are removed from the context.
     *
     * @param identifier the identifier of the entity to be deleted
     */
    public void delete(I identifier) {
        Objects.requireNonNull(identifier, "The entity identifier must not be null");
        try {
            // Optional.ofNullable(find(identifier)).ifPresent(em::remove);
            em.createNativeQuery("""
					DELETE WHERE {
					    GRAPH ?context {
					        ?entity ?r ?o .
					    }
					}
					""")
                    .setParameter("context", entityGraphContext)
                    .setParameter("entity", identifier.toURI())
                    .executeUpdate();
        } catch (RuntimeException e) {
            throw persistenceException(LOG, "Could not delete entity: " + identifier, e);
        }
    }

    /**
     * Checks if an entity with the given identifier exists in the context.
     *
     * @param identifier the identifier of the entity to check for existence
     * @return true if an entity with the given identifier exists in the context, false otherwise
     */
    public boolean exists(I identifier) {
        Objects.requireNonNull(identifier, "The entity identifier must not be null");
        try {
            return em.createNativeQuery("""
					ASK FROM ?context WHERE {
					    ?entity a ?type .
					}
					""", Boolean.class)
                    .setParameter("context", entityGraphContext)
                    .setParameter("entity", identifier.toURI())
                    .setParameter("type", typeUri)
                    .setDescriptor(descriptor)
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw persistenceException(LOG, "Could not check for existence of " + identifier, e);
        }
    }

    /**
     * Finds an entity by its identifier.
     *
     * @param identifier the identifier of the entity to find
     * @return the entity with the given identifier, or null if no such entity exists in the context
     */
    @Nullable public E find(I identifier) {
        try {
            Objects.requireNonNull(identifier);
            return em.find(entityClass, identifier.toURI(), descriptor);
        } catch (RuntimeException e) {
            throw persistenceException(LOG, "Could not find entity " + identifier, e);
        }
    }

    /**
     * Finds entities based on the provided pageable and filter criteria.
     *
     * <p>Each string from the filter is matched against all properties of the entity using a case-insensitive
     * containment check. If the filter list is empty or null, no filtering is applied.
     *
     * @param pageable the pageable containing pagination and sorting information
     * @param filter a list of strings used for filtering entities based on their properties
     * @return a list of entities matching the provided pageable and filter criteria
     */
    public List<E> find(Pageable pageable, List<String> filter) {
        return this.find(pageable, filter, "", q -> {});
    }

    /**
     * Finds entities based on the provided pageable and filter criteria.
     *
     * <p>Each string from the filter is matched against all properties of the entity using a case-insensitive
     * containment check. If the filter list is empty or null, no filtering is applied.
     *
     * @param pageable the pageable containing pagination and sorting information
     * @param filter a list of strings used for filtering entities based on their properties
     * @return a list of entities matching the provided pageable and filter criteria
     */
    public List<E> find(
            Pageable pageable,
            List<String> filter,
            String additionalWhereClause,
            Consumer<TypedQuery<E>> queryCustomizer) {
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
					    %s
					}
					%s
					%s
					""".formatted(
                            buildSelectParams(orders.keySet()),
                            additionalWhereClause,
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
            queryCustomizer.accept(typedQuery);

            return typedQuery.getResultList();
        } catch (RuntimeException e) {
            throw persistenceException(LOG, "Could not retrieve paginated and sorted entities", e);
        }
    }

    public E findReference(I identifier) {
        Objects.requireNonNull(identifier);
        try {
            return em.getReference(entityClass, identifier.toURI());
        } catch (RuntimeException e) {
            throw persistenceException(LOG, "Failed to get reference for entity with identifier: " + identifier, e);
        }
    }

    public URI getTypeUri() {
        return typeUri;
    }

    /**
     * Merges the state of the given entity into the current persistence context.
     *
     * @param entity the entity to be merged
     */
    public void merge(E entity) {
        Objects.requireNonNull(entity);
        try {
            em.merge(entity, descriptor);
        } catch (RuntimeException e) {
            throw persistenceException(LOG, "Failed to merge an entity", e);
        }
    }

    /**
     * Persists the given entity in the context. Throws if the entity already exists in the context.
     *
     * @param entity the entity to be persisted
     */
    public void persist(E entity) {
        Objects.requireNonNull(entity);
        try {
            em.persist(entity, descriptor);
        } catch (RuntimeException e) {
            throw persistenceException(LOG, "Failed to persist an entity", e);
        }
    }

    /**
     * Sets parameters for the given query based on the provided attributes. For each attribute, a parameter with the
     * name "attributeNameType" is set to the IRI of the attribute.
     *
     * @param query the query for which to set the parameters
     * @param attributes the attributes based on which the parameters will be set.
     * @see #buildOrders(Pageable)
     * @see #buildOptionalClause(Iterable)
     */
    protected void setTypeParams(Query query, Iterable<Attribute<?, ?>> attributes) {
        for (Attribute<?, ?> attribute : attributes) {
            query.setParameter(attribute.getName() + "Type", attribute.getIRI());
        }
    }
}
