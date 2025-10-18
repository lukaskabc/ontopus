package cz.lukaskabc.ontology.ontopus.core.service.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyIdentifierResolvingService;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.util.ResultHandlingServiceWrapper;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(ImportProcessServiceOrder.ONTOLOGY_IDENTIFIER_RESOLVING_SERVICE)
public class OntologyIdentifierProcessingService implements OrderedImportPipelineService<Void> {
    private static void setOntologyIdentifier(URI identifier, ImportProcessContext context) {
        context.getVersionSeries().setOntologyIdentifier(identifier);
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
        if (context.getVersionSeries().getOntologyIdentifier() != null) {
            assert context.peekService() == this;
            context.popService();
        }
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
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
            Set<URI> resolved = resolver.resolve(context.getDatabaseContext());
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
