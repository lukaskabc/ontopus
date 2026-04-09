package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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

    public boolean exists(ResourceURI resource, TemporaryContextURI temporaryDatabaseContext) {
        try {
            return em.createNativeQuery("""
					ASK FROM ?context WHERE {
					    ?resource ?p ?o .
					}
					""", Boolean.class)
                    .setParameter("context", temporaryDatabaseContext.toURI())
                    .setParameter("resource", resource.toURI())
                    .getSingleResult();
        } catch (Exception e) {
            throw new PersistenceException(
                    "Failed to check existence of resource " + resource + " in context " + temporaryDatabaseContext, e);
        }
    }

    /**
     * Finds all non-empty language tags present on object values.
     *
     * @param contextUri the database context graph
     * @return the list of language tags
     */
    public List<String> findAllLanguageTags(GraphURI contextUri) {
        Objects.requireNonNull(contextUri);
        try {
            return em.createNativeQuery("""
					SELECT DISTINCT ?language FROM ?context
					WHERE {
					    ?s ?p ?o.
					    BIND(LANG(?o) AS ?language)
					    FILTER(BOUND(?language))
					   	FILTER(?language != "")
					} ORDER BY ?language
					""", String.class)
                    .setParameter("context", contextUri.toURI())
                    .getResultList();
        } catch (Exception e) {
            throw new PersistenceException("Failed to find language tags of graph " + contextUri, e);
        }
    }

    public Stream<URI> findAllSubjects(GraphURI contextUri, Pageable pageable) {
        Objects.requireNonNull(contextUri);
        try {
            return em.createNativeQuery("""
					SELECT DISTINCT ?subject FROM ?context WHERE {
					    ?subject ?predicate ?object .
					} ORDER BY ?subject
					""", URI.class)
                    .setParameter("context", contextUri.toURI())
                    .setMaxResults(pageable.getPageSize())
                    .setFirstResult((int) pageable.getOffset())
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
					} ORDER BY ?subject
					""", URI.class)
                    .setParameter("context", contextUri.toURI())
                    .setParameter("type", type)
                    .getResultStream();
        } catch (Exception e) {
            throw new PersistenceException(
                    "Failed to find all subject of graph " + contextUri + " with type " + type, e);
        }
    }

    /**
     * Finds all triples in a context
     *
     * @param contextUri the graph context
     * @return sorted list of triples
     */
    public List<Statement> findAllTriples(GraphURI contextUri) {
        Objects.requireNonNull(contextUri);
        final Repository repository = em.unwrap(Repository.class);
        final ValueFactory vf = repository.getValueFactory();

        final IRI contextIri = vf.createIRI(contextUri.toString());

        try (RepositoryConnection conn = repository.getConnection()) {
            TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, """
					SELECT ?s ?p ?o
					WHERE {
					    GRAPH ?ctx {
					        ?s ?p ?o .
					    }
					}
					ORDER BY ?s ?p ?o
					""");

            query.setBinding("ctx", contextIri);
            List<Statement> sortedStatements = new ArrayList<>();

            try (TupleQueryResult result = query.evaluate()) {
                while (result.hasNext()) {
                    BindingSet bs = result.next();
                    Resource subject = (Resource) bs.getValue("s");
                    IRI predicate = (IRI) bs.getValue("p");
                    Value object = bs.getValue("o");
                    Statement stmt = vf.createStatement(subject, predicate, object, contextIri);
                    sortedStatements.add(stmt);
                }
            }

            return sortedStatements;
        } catch (Exception e) {
            throw new PersistenceException("Failed to find and sort triples of graph " + contextUri, e);
        }
    }

    /**
     * Finds all triples in a context with a specific subject
     *
     * @param contextUri the graph context
     * @param subject the subject resource
     * @return sorted list of triples
     */
    public List<Statement> findAllWithSubject(GraphURI contextUri, ResourceURI subject) {
        Objects.requireNonNull(contextUri);
        Objects.requireNonNull(subject);
        final Repository repository = em.unwrap(Repository.class);
        final ValueFactory vf = repository.getValueFactory();

        final IRI subjectIri = vf.createIRI(subject.toString());
        final IRI contextIri = vf.createIRI(contextUri.toString());

        try (RepositoryConnection conn = repository.getConnection()) {
            TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, """
					SELECT ?p ?o
					WHERE {
					    GRAPH ?ctx {
					        ?s ?p ?o .
					    }
					}
					ORDER BY ?p ?o
					""");

            query.setBinding("ctx", contextIri);
            query.setBinding("s", subjectIri);

            List<Statement> sortedStatements = new ArrayList<>();

            try (TupleQueryResult result = query.evaluate()) {
                while (result.hasNext()) {
                    BindingSet bs = result.next();
                    IRI predicate = (IRI) bs.getValue("p");
                    Value object = bs.getValue("o");
                    Statement stmt = vf.createStatement(subjectIri, predicate, object, contextIri);
                    sortedStatements.add(stmt);
                }
            }

            return sortedStatements;
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
        final Repository repository = em.unwrap(Repository.class);
        try (final RepositoryConnection conn = repository.getConnection()) {
            conn.begin();
            final IRI graphContext = repository.getValueFactory().createIRI(context.toString());
            log.debug("Importing ontology model into temporary context <{}>", context.toString());
            conn.add(rdfModel, graphContext);
            conn.commit();
        }
    }
}
