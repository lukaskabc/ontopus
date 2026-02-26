package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.GraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void copy(GraphURI source, GraphURI target) {
        graphRepository.copy(source, target);
    }

    /**
     * Moves the content of the source graph to the target graph. If the target graph already exists, it will be dropped
     * before moving as per SPARQL standard.
     *
     * @param source the graph to move from
     * @param target the graph to move to
     */
    @Transactional
    public void move(GraphURI source, GraphURI target) {
        graphRepository.move(source, target);
    }
}
