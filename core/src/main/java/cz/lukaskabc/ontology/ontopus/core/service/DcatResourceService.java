package cz.lukaskabc.ontology.ontopus.core.service;

import cz.cvut.kbss.jopa.model.IRI;
import cz.lukaskabc.ontology.ontopus.api.model.DcatEntityRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.*;
import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ContentNegotiationResolver;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ControllerCandidate;
import cz.lukaskabc.ontology.ontopus.core.service.resource_fallback.ResourceRequestFallbackService;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import cz.lukaskabc.ontology.ontopus.core_model.service.ResourceInContextMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DcatResourceService extends ResourceService {
    /**
     * List of graphs from which resources are publicly accessible
     *
     * @implSpec Every type muse be supported by {@link #submitRequest(ControllerCandidate, DcatEntityRequest)}
     */
    private static final Set<GraphURI> ENTITY_GRAPHS = Stream.of(
                    OntopusCatalog_.entityClassIRI, VersionSeries_.entityClassIRI, VersionArtifact_.entityClassIRI)
            .map(IRI::toURI)
            .map(GraphURIImpl::new)
            .collect(Collectors.toUnmodifiableSet());

    private static final Logger log = LogManager.getLogger(DcatResourceService.class);

    protected static Map<URI, Set<ControllerDescription>> createGraphToControllersMap(
            Set<CatalogController> catalogControllers,
            Set<VersionSeriesController> seriesControllers,
            Set<VersionArtifactController> artifactControllers) {
        return Map.of(
                OntopusCatalog_.entityClassIRI.toURI(),
                mapToDescriptions(catalogControllers),
                VersionSeries_.entityClassIRI.toURI(),
                mapToDescriptions(seriesControllers),
                VersionArtifact_.entityClassIRI.toURI(),
                mapToDescriptions(artifactControllers)
                // Distribution_.entityClassIRI, distributionControllers
                );
    }

    protected static ControllerDescription mapToDescription(NegotiableController controller) {
        ControllerDescription description = new ControllerDescription();
        description.setIdentifier(new ControllerDescriptionURI(Vocabulary.u_i_ontopus_Temporary));
        description.setClassName(controller.getClass().getName());
        description.setSupportedMediaTypes(controller.getSupportedMediaTypes());
        return description;
    }

    protected static Set<ControllerDescription> mapToDescriptions(Set<? extends NegotiableController> controllers) {
        Map<NegotiableController, ControllerDescription> descriptionMap = new HashMap<>();
        return controllers.stream()
                .map(controller -> descriptionMap.computeIfAbsent(controller, DcatResourceService::mapToDescription))
                .collect(Collectors.toSet());
    }

    private final Map<URI, Set<ControllerDescription>> graphToControllersMap;

    private final GraphService graphService;

    public DcatResourceService(
            ApplicationContext applicationContext,
            ContentNegotiationResolver contentNegotiationResolver,
            ResourceInContextMappingService resourceInContextMappingService,
            ContextToControllerMappingService contextToControllerMappingService,
            VersionSeriesService versionSeriesService,
            MediaTypeResolver mediaTypeResolver,
            OntopusConfig ontopusConfig,
            ResourceRequestFallbackService resourceRequestFallbackService,
            Set<CatalogController> catalogControllers,
            Set<VersionSeriesController> seriesControllers,
            Set<VersionArtifactController> artifactControllers,
            Set<DistributionController> distributionControllers,
            GraphService graphService) {
        super(
                applicationContext,
                contentNegotiationResolver,
                resourceInContextMappingService,
                contextToControllerMappingService,
                versionSeriesService,
                mediaTypeResolver,
                ontopusConfig,
                resourceRequestFallbackService);

        this.graphService = graphService;

        graphToControllersMap = createGraphToControllersMap(catalogControllers, seriesControllers, artifactControllers);
        // TODO: distributions?
        if (graphToControllersMap.size() != ENTITY_GRAPHS.size()) {
            throw new InitializationException("Inconsistent mapping of DCAT controllers to DCAT entity graphs!");
        }
    }

    protected GraphURI findEntityGraph(ResourceURI resourceURI) {
        return graphService.findGraphOfEntity(resourceURI, ENTITY_GRAPHS).orElseThrow(() -> NotFoundException.builder()
                .internalMessage("Requested resource not found: " + resourceURI)
                .detailMessageArguments(new Object[] {resourceURI})
                .titleMessageCode("ontopus.core.error.notFound.title")
                .detailMessageCode("ontopus.core.error.notFound.detail")
                .build());
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getResource(
            ResourceURI resourceURI, MediaType @Nullable [] mediaTypes) {
        final GraphURI graphURI = findEntityGraph(resourceURI);
        final Set<ControllerDescription> controllers = graphToControllersMap.get(graphURI.toURI());

        if (controllers == null) {
            throw InternalException.builder()
                    .errorType(Vocabulary.u_i_ontopus_problem_internal_error)
                    .internalMessage("Unknown entity graph: " + graphURI)
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        Optional<ResponseEntity<StreamingResponseBody>> result = Optional.ofNullable(mediaTypes)
                .flatMap(types -> contentNegotiationResolver.resolveController(types, controllers))
                .map(candidate -> {
                    final DcatEntityRequest<?> request =
                            new DcatEntityRequest<>(resourceURI, graphURI, candidate.mediaType());
                    try {
                        return this.submitRequest(candidate.controller(), request);
                    } catch (IllegalStateException | IllegalArgumentException e) {
                        log.error(e.getMessage());
                        return null;
                    }
                })
                .map(ResourceService::cast);

        return result.orElseGet(() -> multipleChoice(controllers, resourceURI));
    }

    /**
     * Submits the request to the appropriate controller based on the graph IRI of the entity request.
     *
     * @param controllerDescription the description of the controller to use
     * @param entityRequest the entity request to submit
     * @return the response entity
     * @throws IllegalArgumentException when unknown graph URI is provided in the request
     * @see #ENTITY_GRAPHS
     */
    @SuppressWarnings("unchecked")
    protected ResponseEntity<StreamingResponseBody> submitRequest(
            ControllerDescription controllerDescription, DcatEntityRequest<?> entityRequest) {
        NegotiableController controller = applicationContext.getBean(getControllerClass(controllerDescription));
        final IRI graphIri = IRI.create(entityRequest.graph().toString());

        if (graphIri.equals(OntopusCatalog_.entityClassIRI)) {
            DcatEntityRequest<OntopusCatalogURI> catalogRequest =
                    entityRequest.withTypedIdentifier(OntopusCatalogURI::new);
            return ((CatalogController) controller).getCatalog(catalogRequest);
        }
        if (graphIri.equals(VersionSeries_.entityClassIRI)) {
            DcatEntityRequest<VersionSeriesURI> seriesRequest =
                    entityRequest.withTypedIdentifier(VersionSeriesURI::new);
            return ((VersionSeriesController) controller).getVersionSeries(seriesRequest);
        }
        if (graphIri.equals(VersionArtifact_.entityClassIRI)) {
            DcatEntityRequest<VersionArtifactURI> artifactRequest =
                    entityRequest.withTypedIdentifier(VersionArtifactURI::new);
            return ((VersionArtifactController) controller).getVersionArtifact(artifactRequest);
        }
        throw new IllegalArgumentException("Unsupported graph IRI: " + graphIri);
    }
}
