package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.ContextToControllerMappingRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ContextToControllerMappingService
        extends BaseService<
                ContextToControllerMappingURI, ContextToControllerMapping, ContextToControllerMappingRepository> {
    public ContextToControllerMappingService(ContextToControllerMappingRepository repository) {
        super(repository);
    }

    protected ContextToControllerMapping createMapping(
            GraphURI contextURI,
            Set<ControllerDescription> controllers,
            MappingType mappingType,
            Set<ContextToControllerMapping> existingMappings) {
        return existingMappings.stream()
                .filter(mapping -> mapping.getMappingType().equals(mappingType))
                .filter(mapping -> mapping.getSubjects().contains(contextURI))
                .findAny()
                .map(mapping -> {
                    mapping.getControllers().addAll(controllers);
                    return mapping;
                })
                .orElseGet(() -> {
                    final ContextToControllerMapping mapping = new ContextToControllerMapping();
                    mapping.addSubject(contextURI);
                    mapping.setMappingType(mappingType);
                    mapping.setControllers(new HashSet<>(controllers));
                    return mapping;
                });
    }

    public ContextToControllerMapping createOntologyMapping(
            GraphURI contextURI,
            Set<ControllerDescription> controllers,
            Set<ContextToControllerMapping> existingMappings) {
        return createMapping(contextURI, controllers, MappingType.ONTOLOGY_DOCUMENT, existingMappings);
    }

    public ContextToControllerMapping createResourceMapping(
            GraphURI contextURI,
            Set<ControllerDescription> controllers,
            Set<ContextToControllerMapping> existingMappings) {
        return createMapping(contextURI, controllers, MappingType.RESOURCE, existingMappings);
    }

    public ContextToControllerMapping findByTypeAndContext(MappingType type, GraphURI contextURI) {
        return repository.findByTypeAndContext(type, contextURI);
    }

    // public ContextToControllerMapping findOntologyMappingByContext(GraphURI
    // graphURI) {
    // return repository.findByTypeAndContext(MappingType.ONTOLOGY_DOCUMENT,
    // graphURI);
    // }
    //
    // public ContextToControllerMapping findResourceMappingByContext(GraphURI
    // graphURI) {
    // return repository.findByTypeAndContext(MappingType.RESOURCE, graphURI);
    // }
}
