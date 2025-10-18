package cz.lukaskabc.ontology.ontopus.plugin.dcatmapper;

import cz.cvut.kbss.jopa.exceptions.OWLPersistenceException;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.query.TypedQuery;
import cz.lukaskabc.ontology.ontopus.core.exception.PersistenceException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class PropertyMapper {
    protected final EntityManager entityManager;

    @Nullable protected final URI subjectURI;

    protected final URI contextURI;

    public PropertyMapper(EntityManager entityManager, @Nullable URI subjectURI, URI contextURI) {
        this.entityManager = entityManager;
        this.subjectURI = subjectURI;
        this.contextURI = contextURI;
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
                .createNativeQuery(
                        """
				SELECT ?object FROM ?context WHERE {
				    ?subject ?predicate ?object .
				    FILTER(?predicate IN (?properties))
				}
				""",
                        resultClass)
                .setParameter("context", contextURI)
                .setParameter("properties", propertiesToMatch);
        if (subjectURI != null) {
            query.setParameter("subject", subjectURI);
        }
        return query;
    }

    protected void mergeMultilingualString(
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
}
