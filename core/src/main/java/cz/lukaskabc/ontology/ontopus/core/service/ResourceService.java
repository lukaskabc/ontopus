package cz.lukaskabc.ontology.ontopus.core.service;

import cz.lukaskabc.ontology.ontopus.api.rest.*;
import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ContentNegotiationResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ControllerCandidate;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.ResourceInContextMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Service
public class ResourceService {

    private final ApplicationContext applicationContext;
    private final ContentNegotiationResolver contentNegotiationResolver;
    private final ResourceInContextMappingService resourceInContextMappingService;
    private final ContextToControllerMappingService contextToControllerMappingService;
    private final VersionSeriesService versionSeriesService;
    private final MediaTypeResolver mediaTypeResolver;

    public ResourceService(
            ApplicationContext applicationContext,
            ContentNegotiationResolver contentNegotiationResolver,
            ResourceInContextMappingService resourceInContextMappingService,
            ContextToControllerMappingService contextToControllerMappingService,
            VersionSeriesService versionSeriesService,
            MediaTypeResolver mediaTypeResolver) {
        this.applicationContext = applicationContext;
        this.contentNegotiationResolver = contentNegotiationResolver;
        this.resourceInContextMappingService = resourceInContextMappingService;
        this.contextToControllerMappingService = contextToControllerMappingService;
        this.versionSeriesService = versionSeriesService;
        this.mediaTypeResolver = mediaTypeResolver;
    }

    private ContextToControllerMapping findControllerMapping(ResourceURI requestedURI, GraphURI graphURI) {
        final MappingType mappingType = resolveMappingType(requestedURI, graphURI);
        return contextToControllerMappingService.findByTypeAndContext(mappingType, graphURI);
    }

    private Class<? extends NegotiableController> getControllerClass(ControllerDescription controller) {
        try {
            return Class.forName(controller.getClassName()).asSubclass(NegotiableController.class);
        } catch (ClassNotFoundException e) {
            throw new OntopusException("Controller class not found: " + controller.getClassName(), e);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<StreamingResponseBody> getResource(
            ResourceURI requestedResource, MediaType[] requestedTypes) {
        final Optional<MediaType> suffixType = resolveSuffixType(requestedResource);
        final MediaType[] mediaTypes =
                suffixType.map(type -> prepend(requestedTypes, type)).orElse(requestedTypes);
        final ResourceURI resourceURI = suffixType.isPresent() ? withoutSuffix(requestedResource) : requestedResource;

        final GraphURI graphURI = resourceInContextMappingService.findRequired(resourceURI);
        ContextToControllerMapping mapping = findControllerMapping(resourceURI, graphURI);

        Optional<ResponseEntity<StreamingResponseBody>> result = contentNegotiationResolver
                .resolveController(mediaTypes, mapping.getControllers())
                .map(candidate -> {
                    final OntopusRequest request = new OntopusRequest(candidate.mediaType(), resourceURI, graphURI);
                    return this.handleRequest(candidate, mapping.getMappingType(), request);
                });

        return result.orElseGet(this::multipleChoice);
    }

    private ResponseEntity<StreamingResponseBody> handleRequest(
            ControllerCandidate candidate, MappingType mappingType, OntopusRequest ontopusRequest) {
        NegotiableController controller = applicationContext.getBean(getControllerClass(candidate.controller()));
        if (mappingType == MappingType.RESOURCE
                && controller instanceof ResourceController<? extends StreamingResponseBody> resourceController) {
            return (ResponseEntity<StreamingResponseBody>) resourceController.handleResourceRequest(ontopusRequest);
        }
        if (mappingType == MappingType.ONTOLOGY_DOCUMENT
                && controller instanceof OntologyController<? extends StreamingResponseBody> ontologyController) {
            return (ResponseEntity<StreamingResponseBody>) ontologyController.handleOntologyRequest(ontopusRequest);
        }
        throw new OntopusException(
                "Controller " + controller.getClass().getName() + " does not support mapping type " + mappingType);
    }

    private ResponseEntity<StreamingResponseBody> multipleChoice() {
        // TODO multiple choice
        return ResponseEntity.status(HttpStatus.MULTIPLE_CHOICES).build();
    }

    private MediaType[] prepend(MediaType[] mediaTypes, MediaType mediaType) {
        final MediaType[] newMediaTypes = new MediaType[mediaTypes.length + 1];
        System.arraycopy(mediaTypes, 0, newMediaTypes, 1, mediaTypes.length);
        newMediaTypes[0] = mediaType;
        return newMediaTypes;
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
