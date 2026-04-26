package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyIdentifierResolvingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ResultHandlingServiceWrapper;
import cz.lukaskabc.ontology.ontopus.core.import_process.OntologyIdentifierSelector;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.*;

@Service
@Order(ImportProcessServiceOrder.ONTOLOGY_IDENTIFIER_RESOLVING_SERVICE)
public class OntologyIdentifierProcessingService implements OrderedImportPipelineService<Void> {

    private static final Logger log = LogManager.getLogger(OntologyIdentifierProcessingService.class);

    private final GraphService graphService;

    private final VersionSeriesService seriesService;
    private final List<OntologyIdentifierResolvingService> resolvers;

    private final ObjectMapper objectMapper;

    public OntologyIdentifierProcessingService(
            List<OntologyIdentifierResolvingService> resolvers,
            ObjectMapper objectMapper,
            GraphService graphService,
            VersionSeriesService seriesService) {
        this.resolvers = resolvers;
        this.objectMapper = objectMapper;
        this.graphService = graphService;
        this.seriesService = seriesService;
    }

    /**
     * Immediately pop itself from the service stack if there already is an ontology identifier.
     *
     * @param context The process context with service stack with this service at the top.
     */
    @Override
    public void afterStackPush(ImportProcessContext context) {
        if (context.getVersionSeries().getIdentifier() != null) {
            if (context.peekService() != this) {
                throw log.throwing(InternalException.unexpectedServiceStackState());
            }
            context.popService();
        }
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        final OntologyURI existingIdentifier = context.getVersionSeries().getOntologyURI();

        // score = number of resolvers that returned the identifier
        final Map<URI, Integer> identifiersWithScores = new HashMap<>();
        for (OntologyIdentifierResolvingService resolver : resolvers) {
            Set<URI> resolved = resolver.resolve(context.getTemporaryDatabaseContext());
            resolved.forEach(uri -> identifiersWithScores.compute(uri, (k, v) -> v == null ? 1 : v + 1));
        }

        // sort identifiers by their scores in descending order
        LinkedHashSet<URI> identifiers = identifiersWithScores.entrySet().stream()
                .sorted(Map.Entry.<URI, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);

        String translationRoot =
                "ontopus.core.service.OrderedImportPipelineService.OntologyIdentifierProcessingService.";

        if (identifiers.isEmpty()) {
            // list all identifiers if no identifier was returned from resolvers
            identifiers = graphService
                    .findAllSubjects(context.getTemporaryDatabaseContext(), Pageable.ofSize(100))
                    .sorted()
                    .collect(LinkedHashSet::new, Set::add, Set::addAll);
            translationRoot += "ontologyNotFound";
        } else {
            translationRoot += "ontologyFound";
        }

        ImportProcessingService<URI> selector = new ResultHandlingServiceWrapper<>(
                new OntologyIdentifierSelector(objectMapper, graphService, identifiers, translationRoot),
                this::setOntologyIdentifier);

        if (context.peekService() != this) {
            throw log.throwing(InternalException.unexpectedServiceStackState());
        }
        context.popService();

        if (existingIdentifier == null || identifiersWithScores.containsKey(existingIdentifier.toURI())) {
            context.pushService(selector);
        }

        return null;
    }

    private void setOntologyIdentifier(URI identifier, ImportProcessContext context) {
        final OntologyURI ontologyURI = new OntologyURI(identifier);
        if (seriesService.isOntologyURI(ontologyURI)) {
            throw JsonFormSubmitException.builder()
                    .errorType(Vocabulary.u_i_already_exists)
                    .internalMessage("Ontology URI already exists")
                    .titleMessageCode("ontopus.core.error.ontologyExists")
                    .detailMessageArguments(new Object[] {identifier})
                    .detailMessageCode("ontopus.core.error.entityExists")
                    .build();
        }
        context.getVersionSeries().setOntologyURI(ontologyURI);
    }
}
