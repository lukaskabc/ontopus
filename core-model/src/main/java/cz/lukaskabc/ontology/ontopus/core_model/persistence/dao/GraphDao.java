package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.Triple;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GraphDao {
    private static final Logger log = LogManager.getLogger(GraphDao.class);
    private final EntityManager em;

    public GraphDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Copies the content of the source graph to the target graph. If the target graph already exists, it will be
     * dropped before copying as per SPARQL standard.
     *
     * @param source the graph to copy from
     * @param target the graph to copy to
     */
    public void copy(GraphURI source, GraphURI target) {
        Objects.requireNonNull(source, "Source graph URI must not be null");
        Objects.requireNonNull(target, "Target graph URI must not be null");
        try {
            em.createNativeQuery("""
					COPY GRAPH ?source TO ?target
					""")
                    .setParameter("source", source.toURI())
                    .setParameter("target", target.toURI())
                    .executeUpdate();
        } catch (Exception e) {
            throw new PersistenceException("Failed to copy graph from " + source + " to " + target, e);
        }
    }

    /**
     * Deletes the specified graph from the database.
     *
     * @param graphUri the URI of the graph to delete
     */
    public void delete(GraphURI graphUri) {
        Objects.requireNonNull(graphUri, "Graph URI must not be null");
        try {
            em.createNativeQuery("""
					DROP GRAPH ?graph
					""").setParameter("graph", graphUri.toURI()).executeUpdate();
        } catch (Exception e) {
            throw new PersistenceException("Failed to delete graph " + graphUri, e);
        }
    }

    public Stream<URI> findAllSubjects(GraphURI contextUri) {
        Objects.requireNonNull(contextUri);
        try {
            return em.createNativeQuery("""
					SELECT DISTINCT ?subject FROM ?context WHERE {
					    ?subject ?predicate ?object .
					}
					""", URI.class)
                    .setParameter("context", contextUri.toURI())
                    .getResultStream();
        } catch (Exception e) {
            throw new PersistenceException("Failed to find all subject of graph " + contextUri, e);
        }
    }

    public Stream<URI> findAllSubjectsOfType(URI type, GraphURI contextUri) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(contextUri);
        try {
            return em.createNativeQuery("""
					SELECT DISTINCT ?subject FROM ?context WHERE {
					    ?subject a ?type .
					}
					""", URI.class)
                    .setParameter("context", contextUri.toURI())
                    .setParameter("type", type)
                    .getResultStream();
        } catch (Exception e) {
            throw new PersistenceException(
                    "Failed to find all subject of graph " + contextUri + " with type " + type, e);
        }
    }

    @SuppressWarnings("unchecked")
    public Stream<Triple> findAllTriples(GraphURI contextUri) {
        Objects.requireNonNull(contextUri);
        try {
            return em.createNativeQuery("""
					SELECT ?subject ?predicate ?object ?context FROM NAMED ?graph WHERE {
					    GRAPH ?context {
					        ?subject ?predicate ?object .
					    }
					}
					""", Triple.MAPPING_NAME)
                    .setParameter("graph", contextUri.toURI())
                    .getResultStream();
        } catch (Exception e) {
            throw new PersistenceException("Failed to find all triples of graph " + contextUri, e);
        }
    }

    @SuppressWarnings("unchecked")
    public Stream<Triple> findAllWithSubject(GraphURI contextUri, ResourceURI subject) {
        Objects.requireNonNull(contextUri);
        Objects.requireNonNull(subject);
        try {
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
        } catch (Exception e) {
            throw new PersistenceException(
                    "Failed to find all subject of graph " + contextUri + " with subject " + subject, e);
        }
    }

    /**
     * Moves the content of the source graph to the target graph. If the target graph already exists, it will be dropped
     * before moving as per SPARQL standard.
     *
     * @param source the graph to move from
     * @param target the graph to move to
     */
    public void move(GraphURI source, GraphURI target) {
        Objects.requireNonNull(source, "Source graph URI must not be null");
        Objects.requireNonNull(target, "Target graph URI must not be null");
        try {
            em.createNativeQuery("""
					MOVE GRAPH ?source TO ?target
					""")
                    .setParameter("source", source.toURI())
                    .setParameter("target", target.toURI())
                    .executeUpdate();
        } catch (Exception e) {
            throw new PersistenceException("Failed to move graph from " + source + " to " + target, e);
        }
    }

    public void persistModel(GraphURI context, Model rdfModel) {
        final Repository repository = em.unwrap(org.eclipse.rdf4j.repository.Repository.class);
        try (final RepositoryConnection conn = repository.getConnection()) {
            conn.begin();
            final IRI graphContext = repository.getValueFactory().createIRI(context.toString());
            log.debug("Importing ontology model into temporary context <{}>", context.toString());
            conn.add(rdfModel, graphContext);
            conn.commit();
        }
    }
}
