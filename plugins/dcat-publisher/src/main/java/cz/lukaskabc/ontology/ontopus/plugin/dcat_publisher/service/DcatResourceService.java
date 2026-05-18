package cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.service;

import cz.cvut.kbss.jopa.model.IRI;
import cz.lukaskabc.ontology.ontopus.api.rest.NegotiableController;
import cz.lukaskabc.ontology.ontopus.api.rest.OntopusRequest;
import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import cz.lukaskabc.ontology.ontopus.core.service.ResourceService;
import cz.lukaskabc.ontology.ontopus.core.service.content_negotiation.ContentNegotiationResolver;
import cz.lukaskabc.ontology.ontopus.core.service.resource_fallback.ResourceRequestFallbackService;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURIImpl;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.GraphRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.ResourceInContextMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.CatalogController;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.DistributionController;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.VersionArtifactController;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest.VersionSeriesController;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DcatResourceService extends ResourceService {
    private static final Set<GraphURI> ENTITY_GRAPHS = Stream.of(
                    OntopusCatalog_.entityClassIRI, VersionSeries_.entityClassIRI, VersionArtifact_.entityClassIRI)
            .map(IRI::toURI)
            .map(GraphURIImpl::new)
            .collect(Collectors.toUnmodifiableSet());

    private final Set<CatalogController> catalogControllers;
    private final Set<VersionSeriesController> seriesControllers;
    private final Set<VersionArtifactController> artifactControllers;
    private final Set<DistributionController> distributionControllers;

    private final Map<URI, Set<? extends NegotiableController>> graphToControllersMap;

    private final GraphRepository graphRepository;

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
            GraphRepository graphRepository) {
        super(
                applicationContext,
                contentNegotiationResolver,
                resourceInContextMappingService,
                contextToControllerMappingService,
                versionSeriesService,
                mediaTypeResolver,
                ontopusConfig,
                resourceRequestFallbackService);

        this.catalogControllers = catalogControllers;
        this.seriesControllers = seriesControllers;
        this.artifactControllers = artifactControllers;
        this.distributionControllers = distributionControllers;
        this.graphRepository = graphRepository;

        graphToControllersMap = Map.of(
                OntopusCatalog_.entityClassIRI.toURI(),
                catalogControllers,
                VersionSeries_.entityClassIRI.toURI(),
                seriesControllers,
                VersionArtifact_.entityClassIRI.toURI(),
                artifactControllers
                // Distribution_.entityClassIRI, distributionControllers
                );
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getResource(
            ResourceURI resourceURI, MediaType @Nullable [] mediaTypes) {
        final GraphURI graphURI = graphRepository
                .findGraphOfEntity(resourceURI, ENTITY_GRAPHS)
                .orElseThrow(() -> NotFoundException.builder()
                        .internalMessage("Requested resource not found: " + resourceURI)
                        .detailMessageArguments(new Object[] {resourceURI})
                        .titleMessageCode("ontopus.core.error.notFound.title")
                        .detailMessageCode("ontopus.core.error.notFound.detail")
                        .build());
        final Set<? extends NegotiableController> controllers = graphToControllersMap.get(graphURI.toURI());

        if (controllers == null) {
            throw InternalException.builder()
                    .errorType(Vocabulary.u_i_internal_error)
                    .internalMessage("Unknown entity graph: " + graphURI)
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        Optional<ResponseEntity<StreamingResponseBody>> result = Optional.ofNullable(mediaTypes)
                .flatMap(types -> contentNegotiationResolver.resolveController(types, controllers))
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
}
