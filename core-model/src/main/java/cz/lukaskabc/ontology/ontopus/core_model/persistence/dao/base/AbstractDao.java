package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base;

import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.IRI;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import cz.cvut.kbss.jopa.model.query.criteria.*;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.TriConsumer;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.util.function.ThrowingSupplier;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractDao<I extends TypedIdentifier, E extends PersistenceEntity<I>> {
    private static final Logger log = LogManager.getLogger(AbstractDao.class);

    private static String buildRegexFilter(@Nullable List<String> filter) {
        if (filter == null || filter.isEmpty()) {
            return "";
        }
        return filter.stream().map(String::toLowerCase).map(Pattern::quote).collect(Collectors.joining("|"));
    }

    public static PersistenceException persistenceException(Logger log, String message, Throwable cause) {
        return log.throwing(PersistenceException.builder()
                .internalMessage(message)
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .cause(cause)
                .build());
    }

    private static PersistenceException persistenceException(String message, Throwable cause) {
        return persistenceException(log, message, cause);
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
            return supplier.get(AbstractDao::persistenceException);
        } catch (NoResultException e) {
            return null;
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

    private <T> void buildFilteredQuery(
            CriteriaQuery<T> query,
            Pageable pageable,
            String filterPattern,
            @Nullable TriConsumer<CriteriaQuery<T>, CriteriaBuilder, Root<E>> queryCustomizer) {
        Objects.requireNonNull(pageable);

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        Root<E> root = query.from(entityClass);

        ParameterExpression<String> pattern = cb.parameter(String.class, "pattern");

        if (!filterPattern.isEmpty()) {
            List<Predicate> filterPredicates = root.getModel().getAttributes().stream()
                    // filter only data properties
                    .filter(attr -> attr.getPersistentAttributeType().equals(Attribute.PersistentAttributeType.DATA))
                    .map(Attribute::getName)
                    // map to attributes of the entity in lowercase
                    .map(name -> cb.lower(root.getAttr(name)))
                    // apply regex pattern matching
                    .map(expr -> cb.like(expr, pattern))
                    .toList();

            Predicate filterMatchesAnyValue = cb.or(filterPredicates.toArray(Predicate[]::new));
            query.where(filterMatchesAnyValue);
        }

        if (!pageable.getSort().isEmpty()) {
            pageable.getSort().forEach(order -> {
                final Path<?> attribute = root.getAttr(order.getProperty());
                Objects.requireNonNull(attribute);

                query.orderBy(order.isAscending() ? cb.asc(attribute) : cb.desc(attribute));
            });
        }

        if (queryCustomizer != null) {
            queryCustomizer.accept(query, cb, root);
        }
    }

    /**
     * Counts the number of entities matching the provided filter criteria.
     *
     * @param filter a list of strings used for filtering entities based on their properties.
     * @return the count of entities matching the filter criteria
     * @see #find(Pageable, List) for details on how the filter is applied to the entities
     */
    public long count(List<String> filter) {
        return this.count(filter, null);
    }

    /**
     * Counts the number of entities matching the provided filter criteria.
     *
     * @param filter a list of strings used for filtering entities based on their properties.
     * @return the count of entities matching the filter criteria
     * @see #find(Pageable, List) for details on how the filter is applied to the entities
     */
    public long count(
            List<String> filter,
            @Nullable TriConsumer<CriteriaQuery<Integer>, CriteriaBuilder, Root<E>> queryCustomizer) {
        try {
            final String filterPattern = buildRegexFilter(filter);
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
            buildFilteredQuery(query, Pageable.unpaged(), filterPattern, queryCustomizer);

            Root<E> root = query.from(entityClass);
            query.distinct().select(cb.count(root));

            final TypedQuery<Integer> typedQuery = em.createQuery(query).setMaxResults(1);

            if (!filterPattern.isEmpty()) {
                typedQuery.setParameter("pattern", filterPattern);
            }

            return typedQuery.getSingleResult();
        } catch (RuntimeException e) {
            throw persistenceException("Could not retrieve paginated and sorted entities", e);
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
            throw persistenceException("Could not delete entity: " + identifier, e);
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
            throw persistenceException("Could not check for existence of " + identifier, e);
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
            throw persistenceException("Could not find entity " + identifier, e);
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
        return this.find(pageable, filter, null);
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
            @Nullable TriConsumer<CriteriaQuery<E>, CriteriaBuilder, Root<E>> queryCustomizer) {

        try {
            final String filterPattern = buildRegexFilter(filter);
            final CriteriaQuery<E> query = em.getCriteriaBuilder().createQuery(entityClass);
            buildFilteredQuery(query, pageable, filterPattern, queryCustomizer);
            query.distinct().select(query.from(entityClass));

            final TypedQuery<E> typedQuery = em.createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());

            if (!filterPattern.isEmpty()) {
                typedQuery.setParameter("pattern", filterPattern);
            }

            return typedQuery.getResultList();
        } catch (RuntimeException e) {
            throw persistenceException("Could not retrieve paginated and sorted entities", e);
        }
    }

    public E findReference(I identifier) {
        Objects.requireNonNull(identifier);
        try {
            return em.getReference(entityClass, identifier.toURI());
        } catch (RuntimeException e) {
            throw persistenceException("Failed to get reference for entity with identifier: " + identifier, e);
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
            throw persistenceException("Failed to merge an entity", e);
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
            throw persistenceException("Failed to persist an entity", e);
        }
    }
}
