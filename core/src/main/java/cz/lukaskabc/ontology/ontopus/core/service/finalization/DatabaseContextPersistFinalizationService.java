package cz.lukaskabc.ontology.ontopus.core.service.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * {@link ImportFinalizingService} that moves the
 */
@Service
@Order(FinalizationServiceOrder.DATABASE_CONTEXT_PERSIST)
public class DatabaseContextPersistFinalizationService implements ImportFinalizingService {

    private final GraphService graphService;

    public DatabaseContextPersistFinalizationService(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public void finalizeImport(ImportProcessContext context) {
        VersionArtifactURI ontologyGraph = Objects.requireNonNull(
                context.getVersionArtifact().getIdentifier(), "Version artifact URI must not be null");
        graphService.move(context.consumeDatabaseContext(), ontologyGraph.toGraphURI());
    }
}
