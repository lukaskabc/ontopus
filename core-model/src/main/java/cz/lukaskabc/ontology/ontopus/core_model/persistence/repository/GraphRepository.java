package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Nullable;
import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

@Repository
public class GraphRepository {
    private final GraphDao graphDao;

    public GraphRepository(GraphDao graphDao) {
        this.graphDao = graphDao;
    }

    /** @see GraphDao#copy(GraphURI, GraphURI) */
    @Transactional
    public void copy(GraphURI source, GraphURI target) {
        graphDao.copy(source, target);
    }

    @Transactional
    public void delete(Collection<Statement> statements, GraphURI context) {
        graphDao.delete(statements, context);
    }

    @Transactional
    public void deleteGraph(GraphURI graphURI) {
        graphDao.delete(graphURI);
    }

    @Transactional(readOnly = true)
    public boolean exists(
            @Nullable ResourceURI subject,
            @Nullable ResourceURI predicate,
            @Nullable ResourceURI object,
            GraphURI context) {
        return graphDao.exists(subject, predicate, object, context);
    }

    /** @see GraphDao#findAllLanguageTags(GraphURI) */
    @Transactional(readOnly = true)
    public List<String> findAllLanguageTags(GraphURI graph) {
        return graphDao.findAllLanguageTags(graph);
    }

    /** @see GraphDao#findAllSubjects(GraphURI, Pageable) */
    @Transactional(readOnly = true)
    public Stream<URI> findAllSubjects(GraphURI graph, Pageable pageable) {
        return graphDao.findAllSubjects(graph, pageable);
    }

    /** @see GraphDao#findAllSubjectsOfType(URI, GraphURI) */
    @Transactional(readOnly = true)
    public Stream<URI> findAllSubjectsOfType(URI type, GraphURI contextUri) {
        return graphDao.findAllSubjectsOfType(type, contextUri);
    }

    /** @see GraphDao#findAllTriples(GraphURI) */
    @Transactional(readOnly = true)
    public List<Statement> findAllTriples(GraphURI contextUri) {
        return graphDao.findAllTriples(contextUri);
    }

    /** @see GraphDao#findAllWithSubject(GraphURI, ResourceURI) */
    @Transactional(readOnly = true)
    public List<Statement> findAllWithSubject(GraphURI contextUri, ResourceURI subject) {
        return graphDao.findAllWithSubject(contextUri, subject);
    }

    /**
     * Finds the graph that contains the triple {@code <resourceURI> rdf:type ?type}.
     *
     * @param resourceURI the resource to lookup
     * @param graphs the set of graphs to search
     */
    @Transactional(readOnly = true)
    public Optional<GraphURI> findGraphOfEntity(ResourceURI resourceURI, @Nullable Set<GraphURI> graphs) {
        graphs = Objects.requireNonNullElseGet(graphs, Set::of);
        return Optional.ofNullable(graphDao.findGraphOfEntity(resourceURI, graphs));
    }

    /** @see GraphDao#move(GraphURI, GraphURI) */
    @Transactional
    public void move(GraphURI source, GraphURI target) {
        graphDao.move(source, target);
    }

    @Transactional
    public void persistModel(GraphURI context, Model rdfModel) {
        graphDao.persistModel(context, rdfModel);
    }
}
