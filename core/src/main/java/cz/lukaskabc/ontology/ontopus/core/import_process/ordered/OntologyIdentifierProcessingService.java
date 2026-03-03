package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyIdentifierResolvingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ResultHandlingServiceWrapper;
import cz.lukaskabc.ontology.ontopus.core.service.process.OntologyIdentifierSelector;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Order(ImportProcessServiceOrder.ONTOLOGY_IDENTIFIER_RESOLVING_SERVICE)
public class OntologyIdentifierProcessingService implements OrderedImportPipelineService<Void> {
    private static void setOntologyIdentifier(URI identifier, ImportProcessContext context) {
        context.getVersionSeries().setIdentifier(new VersionSeriesURI(identifier));
        context.getVersionSeries().setOntologyURI(new OntologyURI(identifier)); // TODO review and rework the ontology
        // URI handling
    }

    private final List<OntologyIdentifierResolvingService> resolvers;

    private final ObjectMapper objectMapper;

    public OntologyIdentifierProcessingService(
            List<OntologyIdentifierResolvingService> resolvers, ObjectMapper objectMapper) {
        this.resolvers = resolvers;
        this.objectMapper = objectMapper;
    }

    /**
     * Immediately pop itself from the service stack if there already is an ontology identifier.
     *
     * @param context The process context with service stack with this service at the top.
     */
    @Override
    public void afterStackPush(ImportProcessContext context) {
        if (context.getVersionSeries().getIdentifier() != null) {
            assert context.peekService() == this;
            context.popService();
        }
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context) {
        return null;
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        Set<URI> identifiers = new HashSet<>();
        for (OntologyIdentifierResolvingService resolver : resolvers) {
            Set<URI> resolved = resolver.resolve(context.getTemporaryDatabaseContext());
            identifiers.addAll(resolved);
        }

        // Create a service that will allow the user to pick an identifier
        // from those automatically resolved, or specify a custom one manually
        // then wrap the service and set the result to the version series
        ImportProcessingService<URI> selector = new ResultHandlingServiceWrapper<>(
                new OntologyIdentifierSelector(objectMapper, identifiers),
                OntologyIdentifierProcessingService::setOntologyIdentifier);

        assert context.peekService() == this;
        context.popService();
        context.pushService(selector);

        return null;
    }
}
