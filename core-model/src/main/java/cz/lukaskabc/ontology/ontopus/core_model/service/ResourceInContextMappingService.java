package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ResourceInContextMapping;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.ResourceInContextMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ResourceInContextMappingService {
    private final ResourceInContextMappingRepository repository;

    public ResourceInContextMappingService(ResourceInContextMappingRepository repository) {
        this.repository = repository;
    }

    public Optional<GraphURI> find(ResourceURI resource) {
        return repository.find(resource).map(ResourceInContextMapping::getObject);
    }

    public GraphURI findRequired(ResourceURI resource) {
        return find(resource).orElseThrow(() -> NotFoundException.builder()
                .internalMessage("No resource in context mapping found for resource: " + resource)
                .build());
    }

    /**
     * Drops all mappings for the given graph and creates new ones based on the content of the source graph.
     *
     * @param sourceGraph the graph for which the mappings should be created
     */
    @Transactional
    public void mapResourcesFromContext(GraphURI sourceGraph) {
        repository.deleteMappingForGraph(sourceGraph);
        repository.mapResourcesFromSourceGraph(sourceGraph);
    }
}
