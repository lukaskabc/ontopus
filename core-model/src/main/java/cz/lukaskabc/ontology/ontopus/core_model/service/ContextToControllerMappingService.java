package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.Controller;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.ContextToControllerMappingRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ContextToControllerMappingService
        extends BaseService<
                ContextToControllerMappingURI, ContextToControllerMapping, ContextToControllerMappingRepository> {
    public ContextToControllerMappingService(ContextToControllerMappingRepository repository) {
        super(repository);
    }

    protected void createMapping(GraphURI contextURI, Set<Controller> controllers, MappingType mappingType) {
        final ContextToControllerMapping mapping = new ContextToControllerMapping();
        mapping.addSubject(contextURI);
        mapping.setMappingType(mappingType);
        mapping.setControllers(controllers);
        repository.persist(mapping);
    }

    public void createOntologyMapping(GraphURI contextURI, Set<Controller> controllers) {
        createMapping(contextURI, controllers, MappingType.ONTOLOGY_DOCUMENT);
    }

    public void createResourceMapping(GraphURI contextURI, Set<Controller> controllers) {
        createMapping(contextURI, controllers, MappingType.RESOURCE);
    }

    public ContextToControllerMapping findOntologyMappingByContext(GraphURI graphURI) {
        return repository.findByTypeAndContext(MappingType.ONTOLOGY_DOCUMENT, graphURI);
    }

    public ContextToControllerMapping findResourceMappingByContext(GraphURI graphURI) {
        return repository.findByTypeAndContext(MappingType.RESOURCE, graphURI);
    }
}
