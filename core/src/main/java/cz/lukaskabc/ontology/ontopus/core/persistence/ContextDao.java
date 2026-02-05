package cz.lukaskabc.ontology.ontopus.core.persistence;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.Triple;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class ContextDao {
    private final EntityManager em;

    public ContextDao(EntityManager em) {
        this.em = em;
    }

    public List<URI> findAllSubjects(URI contextUri) {
        Objects.requireNonNull(contextUri);
        return em.createNativeQuery("""
				SELECT DISTINCT ?subject FROM ?context WHERE {
				    ?subject ?predicate ?object .
				}
				""", URI.class)
                .setParameter("context", contextUri)
                .getResultList();
    }

    public Stream<Triple> findAllTriples(URI contextUri) {
        Objects.requireNonNull(contextUri);
        return em.createNativeQuery("""
				SELECT ?subject ?predicate ?object ?context FROM NAMED ?graph WHERE {
				    GRAPH ?context {
				        ?subject ?predicate ?object .
				    }
				}
				""", Triple.MAPPING_NAME)
                .setParameter("graph", contextUri)
                .getResultStream();
    }
}
