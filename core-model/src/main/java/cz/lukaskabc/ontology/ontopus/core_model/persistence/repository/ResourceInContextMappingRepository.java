package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ResourceInContextMapping;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.ResourceInContextMappingDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ResourceInContextMappingRepository {
    private final ResourceInContextMappingDao dao;

    public ResourceInContextMappingRepository(ResourceInContextMappingDao dao) {
        this.dao = dao;
    }

    /**
     * Deletes all mappings that maps a resource to the given graph.
     *
     * @param graph the graph for which the mappings should be deleted
     */
    @Transactional
    public void deleteMappingForGraph(GraphURI graph) {
        dao.deleteMappingForGraph(graph);
    }

    @Transactional(readOnly = true)
    public Optional<ResourceInContextMapping> find(ResourceURI resource) {
        return Optional.ofNullable(dao.find(resource));
    }

    @Transactional(readOnly = true)
    public Stream<ResourceInContextMapping> findAll(GraphURI graph) {
        return dao.findAll(graph).map(ResourceInContextMapping.class::cast);
    }

    /**
     * Creates mappings for all resources from the {@code sourceGraph} for which no mapping exists.
     *
     * @param sourceGraph the source graph with resources to map
     */
    @Transactional
    public void mapUnmappedResourcesFromSourceGraph(GraphURI sourceGraph) {
        dao.mapUnmappedResourcesFromSourceGraph(sourceGraph);
    }

    /**
     * Deletes all existing mappings of resources from the given {@code sourceGraph}.<br>
     * Inserts mapping for each resource in the given {@code sourceGraph}.
     *
     * <p>Removed are all mappings to <b>any graph</b> for all resources that are <b>subjects</b> in the given
     * {@code sourceGraph}.
     *
     * @param sourceGraph the source graph with resources to map
     */
    @Transactional
    public void remapResourcesFromSourceGraph(GraphURI sourceGraph) {
        dao.deleteExistingMappingsForResourcesFrom(sourceGraph);
        dao.mapResourcesFrom(sourceGraph);
    }
}
