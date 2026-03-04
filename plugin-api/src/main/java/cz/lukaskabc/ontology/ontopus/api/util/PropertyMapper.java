package cz.lukaskabc.ontology.ontopus.api.util;

import cz.cvut.kbss.jopa.exceptions.OWLPersistenceException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.IRI;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import cz.cvut.kbss.ontodriver.model.LangString;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TypedIdentifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PropertyMapper {
    /**
     * Supplies the value from {@code supplier} to {@code consumer} if the current value returned by
     * {@code currentValueSupplier} is null.
     */
    public static <T> void applyMappingWhenNull(
            Supplier<? extends @Nullable T> supplier,
            Consumer<@NonNull T> consumer,
            Supplier<? extends @Nullable T> currentValueSupplier) {
        if (isValueEmpty(currentValueSupplier)) {
            Optional.ofNullable(supplier.get()).ifPresent(consumer);
        }
    }

    public static <T> boolean isValueEmpty(Supplier<? extends @Nullable T> valueSupplier) {
        Object value = valueSupplier.get();
        if (value instanceof Collection<?> collection) {
            return collection.isEmpty();
        }
        if (value instanceof LangString langString) {
            value = langString.getValue();
        }
        if (value instanceof CharSequence sequence) {
            return !StringUtils.hasText(sequence);
        }
        if (value instanceof MultilingualString multilingualString) {
            return multilingualString.getValue().isEmpty();
        }
        return value == null;
    }

    public static Set<URI> mapAttributes(Attribute<?, ?>... propertiesToMatch) {
        return Arrays.stream(propertiesToMatch)
                .map(Attribute::getIRI)
                .map(IRI::toURI)
                .collect(Collectors.toSet());
    }

    public static void mergeMultilingualString(
            Supplier<@Nullable MultilingualString> getter,
            Consumer<MultilingualString> setter,
            MultilingualString multilingualString) {
        MultilingualString current = getter.get();
        if (current == null) {
            current = new MultilingualString();
        }

        current.getValue().putAll(multilingualString.getValue());

        setter.accept(current);
    }

    protected final EntityManager entityManager;

    @Nullable protected final URI subjectURI;

    protected final URI contextURI;

    public PropertyMapper(EntityManager entityManager, @Nullable ResourceURI subjectURI, GraphURI contextURI) {
        this.entityManager = entityManager;
        this.subjectURI =
                Optional.ofNullable(subjectURI).map(TypedIdentifier::toURI).orElse(null);
        this.contextURI = Objects.requireNonNull(contextURI).toURI();
    }

    public MultilingualString findMultilingualProperty(Set<URI> propertiesToMatch) {
        final MultilingualString result = new MultilingualString();
        findProperties(propertiesToMatch, LangString.class).forEach(property -> {
            property.getLanguage()
                    .ifPresentOrElse(
                            lang -> result.set(lang, property.getValue()), () -> result.set(property.getValue()));
        });
        return result;
    }

    /**
     * Finds all object values matching all {@code propertiesToMatch} on {@code subject}. Values are mapped to
     * {@code resultClass}.
     *
     * @param propertiesToMatch properties to match on the subject
     * @param resultClass type to which the result values should be mapped
     * @return the list of results mapped to {@code resultClass}
     * @param <T> The type of the result values
     */
    public <T> List<T> findProperties(Set<URI> propertiesToMatch, Class<T> resultClass) {
        try {
            return getPropertyQuery(propertiesToMatch, resultClass).getResultList();
        } catch (OWLPersistenceException e) {
            return List.of();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Finds first object value matching any {@code propertiesToMatch} on {@code subject}. Value is mapped to
     * {@code resultClass}.
     *
     * @param propertiesToMatch properties to match on the subject
     * @param resultClass type to which the result value should be mapped
     * @return the result mapped to {@code resultClass}
     * @param <T> The type of the result values
     */
    @Nullable public <T> T findSingleProperty(Set<URI> propertiesToMatch, Class<T> resultClass) {
        try {
            return getPropertyQuery(propertiesToMatch, resultClass)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (OWLPersistenceException e) {
            return null;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    protected <T> TypedQuery<T> getPropertyQuery(Set<URI> propertiesToMatch, Class<T> resultClass) {
        final TypedQuery<T> query = entityManager
                .createNativeQuery("""
				SELECT ?object FROM ?context WHERE {
				    ?subject ?predicate ?object .
				    FILTER(?predicate IN (?properties))
				}
				""", resultClass)
                .setParameter("context", contextURI)
                .setParameter("properties", propertiesToMatch);
        if (subjectURI != null) {
            query.setParameter("subject", subjectURI);
        }
        return query;
    }
}
