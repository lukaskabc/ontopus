package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.rest.*;
import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ContentNegotiationResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ControllerCandidate;
import cz.lukaskabc.ontology.ontopus.core.service.resource_fallback.HttpsSchemaFallbackService;
import cz.lukaskabc.ontology.ontopus.core.service.resource_fallback.ResourceRequestFallbackService;
import cz.lukaskabc.ontology.ontopus.core.service.resource_fallback.TrailingSlashFallbackService;
import cz.lukaskabc.ontology.ontopus.core.util.MultipleChoiceResponseWriter;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ResourceService extends ResourceRequestFallbackService {

    private static final Logger log = LogManager.getLogger(ResourceService.class);

    @SuppressWarnings("unchecked")
    private static ResponseEntity<StreamingResponseBody> cast(
            ResponseEntity<? extends StreamingResponseBody> response) {
        return (ResponseEntity<StreamingResponseBody>) response;
    }

    private final OntopusConfig ontopusConfig;

    private final ApplicationContext applicationContext;
    private final ContentNegotiationResolver contentNegotiationResolver;
    private final ResourceInContextMappingService resourceInContextMappingService;
    private final ContextToControllerMappingService contextToControllerMappingService;
    private final VersionSeriesService versionSeriesService;

    private final MediaTypeResolver mediaTypeResolver;

    private final ResourceRequestFallbackService resourceRequestFallbackService;

    public ResourceService(
            ApplicationContext applicationContext,
            ContentNegotiationResolver contentNegotiationResolver,
            ResourceInContextMappingService resourceInContextMappingService,
            ContextToControllerMappingService contextToControllerMappingService,
            VersionSeriesService versionSeriesService,
            MediaTypeResolver mediaTypeResolver,
            OntopusConfig ontopusConfig) {
        super(null);
        this.applicationContext = applicationContext;
        this.contentNegotiationResolver = contentNegotiationResolver;
        this.resourceInContextMappingService = resourceInContextMappingService;
        this.contextToControllerMappingService = contextToControllerMappingService;
        this.versionSeriesService = versionSeriesService;
        this.mediaTypeResolver = mediaTypeResolver;
        this.ontopusConfig = ontopusConfig;

        resourceRequestFallbackService =
                new HttpsSchemaFallbackService(new TrailingSlashFallbackService(this, ontopusConfig), ontopusConfig);
    }

    private ContextToControllerMapping findControllerMapping(ResourceURI requestedURI, GraphURI graphURI) {
        final MappingType mappingType = resolveMappingType(requestedURI, graphURI);
        return contextToControllerMappingService.findByTypeAndContext(mappingType, graphURI);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<StreamingResponseBody> findResource(
            ResourceURI requestedResource, MediaType @Nullable [] requestedTypes) {
        final Optional<MediaType> suffixType = resolveSuffixType(requestedResource);
        final MediaType[] mediaTypes =
                suffixType.map(type -> new MediaType[] {type}).orElse(requestedTypes);
        final ResourceURI resourceURI = suffixType.isPresent() ? withoutSuffix(requestedResource) : requestedResource;

        return resourceRequestFallbackService.getResource(resourceURI, mediaTypes);
    }

    private Class<? extends NegotiableController> getControllerClass(ControllerDescription controller) {
        try {
            return Class.forName(controller.getClassName()).asSubclass(NegotiableController.class);
        } catch (ClassNotFoundException e) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_internal_error)
                    .internalMessage("Controller class not found: " + controller.getClassName())
                    .cause(e)
                    .build());
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getResource(
            ResourceURI resourceURI, MediaType @Nullable [] mediaTypes) {
        final GraphURI graphURI = resourceInContextMappingService.findRequired(resourceURI);
        ContextToControllerMapping mapping = findControllerMapping(resourceURI, graphURI);

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

        return result.orElseGet(() -> multipleChoice(mapping, resourceURI));
    }

    private ResponseEntity<? extends StreamingResponseBody> handleRequest(
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
                .errorType(Vocabulary.u_i_not_supported)
                .internalMessage("Controller " + controller.getClass().getName()
                        + " does not support the requested mapping type: " + mappingType.name())
                .titleMessageCode("ontopus.core.error.mapping.failed")
                .build());
    }

    private ResponseEntity<StreamingResponseBody> multipleChoice(
            ContextToControllerMapping mapping, ResourceURI resourceURI) {
        Map<String, MediaType> supportedExtensions = resolveSupportedFileExtensions(mapping);
        if (supportedExtensions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        return ResponseEntity.status(HttpStatus.MULTIPLE_CHOICES)
                .contentType(MediaType.TEXT_HTML)
                .body(new MultipleChoiceResponseWriter(supportedExtensions, resourceURI, ontopusConfig));
    }

    private MappingType resolveMappingType(ResourceURI requestedURI, GraphURI graphURI) {
        if (requestedURI.equals(graphURI)) {
            return MappingType.ONTOLOGY_DOCUMENT;
        }
        final boolean isOntologyURI = versionSeriesService.isOntologyURI(requestedURI);
        if (isOntologyURI) {
            return MappingType.ONTOLOGY_DOCUMENT;
        }
        return MappingType.RESOURCE;
    }

    private Optional<MediaType> resolveSuffixType(ResourceURI resourceURI) {
        final String extension =
                StringUtils.getFilenameExtension(resourceURI.toURI().getPath());
        if (extension == null) {
            return Optional.empty();
        }
        return mediaTypeResolver.resolveMediaType(extension);
    }

    private Map<String, MediaType> resolveSupportedFileExtensions(ContextToControllerMapping mapping) {
        Map<String, MediaType> fileExtensions = new HashMap<>();
        for (ControllerDescription controller : mapping.getControllers()) {
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

    private ResourceURI withoutSuffix(ResourceURI resourceURI) {
        final URI original = resourceURI.toURI();
        final String fileExt = StringUtils.getFilenameExtension(original.getPath());
        if (fileExt == null) {
            return resourceURI;
        }

        String originalPath = original.getPath();
        String newPath = originalPath.substring(0, originalPath.length() - fileExt.length() - 1);

        URI newUri = UriComponentsBuilder.fromUri(original)
                .replacePath(newPath)
                .build()
                .toUri();
        return new ResourceURI(newUri);
    }
}
