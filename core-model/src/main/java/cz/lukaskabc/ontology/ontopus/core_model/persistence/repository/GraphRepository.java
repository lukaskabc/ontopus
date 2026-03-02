package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.Triple;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
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

    /** @see GraphDao#findAllSubjects(GraphURI) */
    @Transactional(readOnly = true)
    public Stream<URI> findAllSubjects(GraphURI graph) {
        return graphDao.findAllSubjects(graph);
    }

    /** @see GraphDao#findAllSubjectsOfType(URI, GraphURI) */
    @Transactional(readOnly = true)
    public Stream<URI> findAllSubjectsOfType(URI type, GraphURI contextUri) {
        return graphDao.findAllSubjectsOfType(type, contextUri);
    }

    /** @see GraphDao#findAllTriples(GraphURI) */
    @Transactional(readOnly = true)
    public Stream<Triple> findAllTriples(GraphURI contextUri) {
        return graphDao.findAllTriples(contextUri);
    }

    /** @see GraphDao#findAllWithSubject(GraphURI, ResourceURI) */
    @Transactional(readOnly = true)
    public Stream<Triple> findAllWithSubject(GraphURI contextUri, ResourceURI subject) {
        return graphDao.findAllWithSubject(contextUri, subject);
    }

    /** @see GraphDao#move(GraphURI, GraphURI) */
    @Transactional
    public void move(GraphURI source, GraphURI target) {
        graphDao.move(source, target);
    }
}
