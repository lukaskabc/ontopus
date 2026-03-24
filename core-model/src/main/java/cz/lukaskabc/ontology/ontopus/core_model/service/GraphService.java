package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.GraphRepository;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

@Service
public class GraphService {
    private final GraphRepository graphRepository;

    public GraphService(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    /**
     * Copies the content of the source graph to the target graph. If the target graph already exists, it will be
     * dropped before copying as per SPARQL standard.
     *
     * @param source the graph to copy from
     * @param target the graph to copy to
     */
    public void copy(GraphURI source, GraphURI target) {
        graphRepository.copy(source, target);
    }

    /** @see GraphDao#findAllSubjects(GraphURI) */
    public Stream<URI> findAllSubjects(GraphURI graph) {
        return graphRepository.findAllSubjects(graph);
    }

    public Stream<URI> findAllSubjectsOfType(URI type, GraphURI contextUri) {
        return graphRepository.findAllSubjectsOfType(type, contextUri);
    }

    /** @see GraphDao#findAllTriples(GraphURI) */
    public List<Statement> findAllTriples(GraphURI contextUri) {
        return graphRepository.findAllTriples(contextUri);
    }

    /** @see GraphDao#findAllWithSubject(GraphURI, ResourceURI) */
    public List<Statement> findAllWithSubject(GraphURI contextUri, ResourceURI subject) {
        return graphRepository.findAllWithSubject(contextUri, subject);
    }

    /**
     * Moves the content of the source graph to the target graph. If the target graph already exists, it will be dropped
     * before moving as per SPARQL standard.
     *
     * @param source the graph to move from
     * @param target the graph to move to
     */
    public void move(GraphURI source, GraphURI target) {
        graphRepository.move(source, target);
    }
}
