package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.rest.NegotiableController;
import cz.lukaskabc.ontology.ontopus.api.rest.OntologyController;
import cz.lukaskabc.ontology.ontopus.api.rest.OntopusRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.ResourceController;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ContentNegotiationResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ControllerCandidate;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.Controller;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.ResourceInContextMappingService;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.Optional;

@Service
public class ResourceService {

    private final ApplicationContext applicationContext;
    private final ContentNegotiationResolver contentNegotiationResolver;
    private final ResourceInContextMappingService resourceInContextMappingService;
    private final ContextToControllerMappingService contextToControllerMappingService;

    public ResourceService(
            ApplicationContext applicationContext,
            ContentNegotiationResolver contentNegotiationResolver,
            ResourceInContextMappingService resourceInContextMappingService,
            ContextToControllerMappingService contextToControllerMappingService) {
        this.applicationContext = applicationContext;
        this.contentNegotiationResolver = contentNegotiationResolver;
        this.resourceInContextMappingService = resourceInContextMappingService;
        this.contextToControllerMappingService = contextToControllerMappingService;
    }

    private Class<? extends NegotiableController> getControllerClass(Controller controller) {
        try {
            return Class.forName(controller.getClassName()).asSubclass(NegotiableController.class);
        } catch (ClassNotFoundException e) {
            throw new OntopusException("Controller class not found: " + controller.getClassName(), e);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<StreamingResponseBody> getResource(ResourceURI resourceURI, MediaType[] requestedTypes) {
        final GraphURI graphURI = resourceInContextMappingService.findRequired(resourceURI);
        ContextToControllerMapping mapping = contextToControllerMappingService.findResourceMappingByContext(graphURI);

        Optional<ResponseEntity<StreamingResponseBody>> result = contentNegotiationResolver
                .resolveController(requestedTypes, mapping.getControllers())
                .map(candidate -> {
                    final OntopusRequest request = new OntopusRequest(candidate.mediaType(), resourceURI, graphURI);
                    return this.handleRequest(candidate, mapping.getMappingType(), request);
                });

        if (result.isPresent()) {
            return result.get();
        } else {
            return multipleChoice();
        }
    }

    private ResponseEntity<StreamingResponseBody> handleRequest(
            ControllerCandidate candidate, MappingType mappingType, OntopusRequest ontopusRequest) {
        NegotiableController controller = applicationContext.getBean(getControllerClass(candidate.controller()));
        if (mappingType == MappingType.RESOURCE && controller instanceof ResourceController<?> resourceController) {
            return (ResponseEntity<StreamingResponseBody>) resourceController.handleResourceRequest(ontopusRequest);
        }
        if (mappingType == MappingType.ONTOLOGY_DOCUMENT
                && controller instanceof OntologyController<?> ontologyController) {
            return (ResponseEntity<StreamingResponseBody>) ontologyController.handleOntologyRequest(ontopusRequest);
        }
        throw new OntopusException(
                "Controller " + controller.getClass().getName() + " does not support mapping type " + mappingType);
    }

    private ResponseEntity<StreamingResponseBody> multipleChoice() {
        return (ResponseEntity<StreamingResponseBody>) ResponseEntity.notFound();
    }
}
