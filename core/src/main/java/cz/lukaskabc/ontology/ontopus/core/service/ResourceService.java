package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.model.OntopusRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.NegotiableController;
import cz.lukaskabc.ontology.ontopus.api.rest.OntologyController;
import cz.lukaskabc.ontology.ontopus.api.rest.ResourceController;
import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ContentNegotiationResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ControllerCandidate;
import cz.lukaskabc.ontology.ontopus.core.service.resource_fallback.ResourceRequestFallbackService;
import cz.lukaskabc.ontology.ontopus.core.util.MultipleChoiceResponseWriter;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.ResourceInContextMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Primary
public class ResourceService {

    private static final Logger log = LogManager.getLogger(ResourceService.class);

    @SuppressWarnings("unchecked")
    protected static ResponseEntity<StreamingResponseBody> cast(
            ResponseEntity<? extends StreamingResponseBody> response) {
        return (ResponseEntity<StreamingResponseBody>) response;
    }

    protected final OntopusConfig ontopusConfig;

    protected final ApplicationContext applicationContext;
    protected final ContentNegotiationResolver contentNegotiationResolver;
    protected final ResourceInContextMappingService resourceInContextMappingService;
    protected final ContextToControllerMappingService contextToControllerMappingService;
    protected final VersionSeriesService versionSeriesService;

    protected final MediaTypeResolver mediaTypeResolver;

    protected final ResourceRequestFallbackService resourceRequestFallbackService;
    protected final MediaType fallbackType;

    public ResourceService(
            ApplicationContext applicationContext,
            ContentNegotiationResolver contentNegotiationResolver,
            ResourceInContextMappingService resourceInContextMappingService,
            ContextToControllerMappingService contextToControllerMappingService,
            VersionSeriesService versionSeriesService,
            MediaTypeResolver mediaTypeResolver,
            OntopusConfig ontopusConfig,
            ResourceRequestFallbackService resourceRequestFallbackService) {
        this.applicationContext = applicationContext;
        this.contentNegotiationResolver = contentNegotiationResolver;
        this.resourceInContextMappingService = resourceInContextMappingService;
        this.contextToControllerMappingService = contextToControllerMappingService;
        this.versionSeriesService = versionSeriesService;
        this.mediaTypeResolver = mediaTypeResolver;
        this.ontopusConfig = ontopusConfig;
        this.resourceRequestFallbackService = resourceRequestFallbackService;
        this.fallbackType = ontopusConfig.getResource().getFallbackMediatype();
    }

    private ContextToControllerMapping findControllerMapping(ResourceURI requestedURI, GraphURI graphURI) {
        final MappingType mappingType = resolveMappingType(requestedURI, graphURI);
        return contextToControllerMappingService.findByTypeAndContext(mappingType, graphURI);
    }

    /**
     * Optionally resolves file extension from the requested resource URI suffix<br>
     * and calls {@link #getResource(ResourceURI, MediaType[])} with fallbacks {@link #resourceRequestFallbackService}
     *
     * @param requestedResource the requested resource
     * @param requestedTypes the requested types from accept header
     * @return the response
     */
    @Transactional(readOnly = true)
    public ResponseEntity<StreamingResponseBody> findResource(
            ResourceURI requestedResource, MediaType @Nullable [] requestedTypes) {
        final Optional<MediaType> suffixType = mediaTypeResolver.resolveSuffixType(requestedResource.toURI());
        final MediaType[] mediaTypes =
                suffixType.map(type -> new MediaType[] {type}).orElse(requestedTypes);
        final ResourceURI resourceURI =
                suffixType.isPresent() ? StringUtils.withoutSuffix(requestedResource) : requestedResource;

        replaceUniversalMediaType(mediaTypes);

        return resourceRequestFallbackService.withFallback(
                resourceURI, (fallbackUri) -> getResource(fallbackUri, mediaTypes));
    }

    protected Class<? extends NegotiableController> getControllerClass(ControllerDescription controller) {
        try {
            return Class.forName(controller.getClassName()).asSubclass(NegotiableController.class);
        } catch (ClassNotFoundException e) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_ontopus_problem_internal_error)
                    .internalMessage("Controller class not found: " + controller.getClassName())
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .cause(e)
                    .build());
        }
    }

    /**
     * Finds the requested resource and resolves the most preferred media type.
     *
     * @param resourceURI the requested resource
     * @param mediaTypes acceptable media types
     * @return the resource or multiple choice if no media type matched
     */
    public ResponseEntity<StreamingResponseBody> getResource(
            ResourceURI resourceURI, MediaType @Nullable [] mediaTypes) {
        final GraphURI graphURI = resourceInContextMappingService.findRequired(resourceURI);
        ContextToControllerMapping mapping = findControllerMapping(resourceURI, graphURI);

        log.debug("Mapped resource <{}> to context <{}>", resourceURI, graphURI);

        Optional<ResponseEntity<StreamingResponseBody>> result = Optional.ofNullable(mediaTypes)
                .flatMap(types -> contentNegotiationResolver.resolveController(types, mapping.getControllers()))
                .map(candidate -> {
                    final OntopusRequest request = new OntopusRequest(
                            candidate.mediaType(), resourceURI, new OntologyVersionURI(graphURI.toURI()));
                    try {
                        return this.handleRequest(candidate, mapping.getMappingType(), request);
                    } catch (IllegalStateException e) {
                        log.error(e.getMessage());
                        return null;
                    }
                })
                .map(ResourceService::cast);

        return result.orElseGet(() -> multipleChoice(mapping.getControllers(), resourceURI));
    }

    protected ResponseEntity<? extends StreamingResponseBody> handleRequest(
            ControllerCandidate candidate, MappingType mappingType, OntopusRequest ontopusRequest) {
        NegotiableController controller = applicationContext.getBean(getControllerClass(candidate.controller()));
        if (mappingType == MappingType.RESOURCE
                && controller instanceof ResourceController<? extends StreamingResponseBody> resourceController) {
            return resourceController.handleResourceRequest(ontopusRequest);
        }
        if (mappingType == MappingType.ONTOLOGY_DOCUMENT
                && controller instanceof OntologyController<? extends StreamingResponseBody> ontologyController) {
            return ontologyController.handleOntologyRequest(ontopusRequest);
        }

        throw log.throwing(InternalException.builder()
                .errorType(Vocabulary.u_i_ontopus_problem_not_supported)
                .internalMessage("Controller " + controller.getClass().getName()
                        + " does not support the requested mapping type: " + mappingType.name())
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .titleMessageCode("ontopus.core.error.mapping.failed")
                .build());
    }

    protected ResponseEntity<StreamingResponseBody> multipleChoice(
            Collection<ControllerDescription> controllerDescriptions, ResourceURI resourceURI) {
        Map<String, MediaType> supportedExtensions = resolveSupportedFileExtensions(controllerDescriptions);
        if (supportedExtensions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        return ResponseEntity.status(HttpStatus.MULTIPLE_CHOICES)
                .contentType(MediaType.TEXT_HTML)
                .body(new MultipleChoiceResponseWriter(supportedExtensions, resourceURI, ontopusConfig));
    }

    protected void replaceUniversalMediaType(MediaType @Nullable [] mediaTypes) {
        if (mediaTypes == null) {
            return;
        }
        for (int i = 0; i < mediaTypes.length; i++) {
            final MediaType requested = mediaTypes[i];
            if (MediaType.ALL.equalsTypeAndSubtype(requested)) {
                mediaTypes[i] = fallbackType.copyQualityValue(requested);
            }
        }
    }

    protected MappingType resolveMappingType(ResourceURI requestedURI, GraphURI graphURI) {
        if (requestedURI.equals(graphURI)) {
            return MappingType.ONTOLOGY_DOCUMENT;
        }
        final boolean isOntologyURI = versionSeriesService.isOntologyURI(requestedURI);
        if (isOntologyURI) {
            return MappingType.ONTOLOGY_DOCUMENT;
        }
        return MappingType.RESOURCE;
    }

    protected Map<String, MediaType> resolveSupportedFileExtensions(
            Collection<ControllerDescription> controllerDescriptions) {
        Map<String, MediaType> fileExtensions = new HashMap<>();
        for (ControllerDescription controller : controllerDescriptions) {
            for (MediaType type : controller.getSupportedMediaTypes()) {
                List<String> extensions = mediaTypeResolver.resolveFileExtensions(type);
                if (extensions.isEmpty()) {
                    continue;
                }
                final String ext = extensions.getFirst();
                fileExtensions.compute(ext, (_, existing) -> {
                    if (existing == null || type.isMoreSpecific(existing)) {
                        return type;
                    }
                    return existing;
                });
            }
        }
        return fileExtensions;
    }
}
