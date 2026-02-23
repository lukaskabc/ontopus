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

    @Transactional
    public void deleteMappingForGraph(GraphURI graph) {
        dao.deleteMappingForGraph(graph);
    }

    @Transactional
    public Optional<ResourceInContextMapping> find(ResourceURI resource) {
        return Optional.ofNullable(dao.find(resource));
    }

    @Transactional
    public Stream<ResourceInContextMapping> findAll(GraphURI graph) {
        return dao.findAll(graph).map(ResourceInContextMapping.class::cast);
    }

    @Transactional
    public void mapResourcesFromSourceGraph(GraphURI sourceGraph) {
        dao.deleteExistingMappingsForResourcesFrom(sourceGraph);
        dao.mapResourcesFrom(sourceGraph);
    }
}
