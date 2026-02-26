package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.Triple;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public List<URI> findAllSubjects(GraphURI contextUri) {
        Objects.requireNonNull(contextUri);
        return em.createNativeQuery("""
				SELECT DISTINCT ?subject FROM ?context WHERE {
				    ?subject ?predicate ?object .
				}
				""", URI.class)
                .setParameter("context", contextUri.toURI())
                .getResultList();
    }

    @Transactional
    public Stream<Triple> findAllTriples(GraphURI contextUri) {
        Objects.requireNonNull(contextUri);
        return em.createNativeQuery("""
				SELECT ?subject ?predicate ?object ?context FROM NAMED ?graph WHERE {
				    GRAPH ?context {
				        ?subject ?predicate ?object .
				    }
				}
				""", Triple.MAPPING_NAME)
                .setParameter("graph", contextUri.toURI())
                .getResultStream();
    }

    @Transactional
    public Stream<Triple> findAllWithSubject(GraphURI contextUri, ResourceURI subject) {
        Objects.requireNonNull(contextUri);
        Objects.requireNonNull(subject);
        return em.createNativeQuery("""
				SELECT ?subject ?predicate ?object ?context FROM NAMED ?graph WHERE {
				    GRAPH ?context {
				        ?subject ?predicate ?object .
				    }
				}
				""", Triple.MAPPING_NAME)
                .setParameter("graph", contextUri.toURI())
                .setParameter("subject", subject.toURI())
                .getResultStream();
    }
}
