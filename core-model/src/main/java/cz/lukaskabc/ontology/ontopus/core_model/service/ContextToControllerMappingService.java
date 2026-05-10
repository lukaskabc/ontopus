package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
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

        // attempt to reuse existing mapping
        return existingMappings.stream()
                .filter(mapping -> mapping.getMappingType().equals(mappingType))
                .filter(mapping -> mapping.getSubject().equals(contextURI))
                .findAny()
                .map(mapping -> {
                    mapping.getControllers().addAll(controllers);
                    return mapping;
                })
                // attempt to reuse existing mapping from database
                .or(() -> repository.findByTypeAndContext(MappingType.ONTOLOGY_DOCUMENT, contextURI))
                // replace controllers overriding data from database
                .map(existing -> existing.setControllers(new HashSet<>(controllers)))
                .orElseGet(() -> {
                    final ContextToControllerMapping mapping = new ContextToControllerMapping();
                    mapping.setSubject(contextURI);
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

    public void deleteBySubject(GraphURI graphURI) {
        repository.deleteBySubject(graphURI);
    }

    public ContextToControllerMapping findByTypeAndContext(MappingType type, GraphURI contextURI) {
        return repository.findByTypeAndContext(type, contextURI).orElseThrow(() -> NotFoundException.builder()
                .internalMessage("ContextToControllerMapping not found for type " + type + " and context " + contextURI)
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .titleMessageCode("ontopus.core.error.notFound.title")
                .build());
    }
}
