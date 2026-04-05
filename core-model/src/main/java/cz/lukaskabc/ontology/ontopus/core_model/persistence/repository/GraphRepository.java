package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
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

    public boolean exists(ResourceURI resource, TemporaryContextURI temporaryDatabaseContext) {
        return graphDao.exists(resource, temporaryDatabaseContext);
    }

    /** @see GraphDao#findAllLanguageTags(GraphURI) */
    @Transactional(readOnly = true)
    public List<String> findAllLanguageTags(GraphURI graph) {
        return graphDao.findAllLanguageTags(graph);
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
    public List<Statement> findAllTriples(GraphURI contextUri) {
        return graphDao.findAllTriples(contextUri);
    }

    /** @see GraphDao#findAllWithSubject(GraphURI, ResourceURI) */
    @Transactional(readOnly = true)
    public List<Statement> findAllWithSubject(GraphURI contextUri, ResourceURI subject) {
        return graphDao.findAllWithSubject(contextUri, subject);
    }

    /** @see GraphDao#move(GraphURI, GraphURI) */
    @Transactional
    public void move(GraphURI source, GraphURI target) {
        graphDao.move(source, target);
    }
}
