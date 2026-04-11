package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.GraphDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.GraphRepository;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Collection;
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

    public void delete(Collection<Statement> statements, GraphURI graphURI) {
        graphRepository.delete(statements, graphURI);
    }

    /** @see GraphDao#findAllLanguageTags(GraphURI) */
    @Transactional(readOnly = true)
    public List<String> findAllLanguageTags(GraphURI graph) {
        return graphRepository.findAllLanguageTags(graph);
    }

    /** @see GraphDao#findAllSubjects(GraphURI, Pageable) */
    public Stream<URI> findAllSubjects(GraphURI graph, Pageable pageable) {
        return graphRepository.findAllSubjects(graph, pageable);
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

    public void persistModel(GraphURI context, Model rdfModel) {
        graphRepository.persistModel(context, rdfModel);
    }

    /**
     * Checks whether the given predicate exists on the given subject
     *
     * @return true when the predicate exists on the given subject
     */
    public boolean predicateExists(ResourceURI subject, ResourceURI predicate, GraphURI graphURI) {
        return graphRepository.exists(subject, predicate, null, graphURI);
    }

    /**
     * Checks whether the given subject exists in the graph
     *
     * @return true when the resource exists
     */
    public boolean subjectExists(ResourceURI resource, GraphURI graphURI) {
        return graphRepository.exists(resource, null, null, graphURI);
    }
}
