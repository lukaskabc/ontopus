package cz.lukaskabc.ontology.ontopus.core.service.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * {@link ImportFinalizingService} that moves the temporary graph to the ontology graph identified by the ontology
 * version URI
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
        graphService.move(context.consumeDatabaseContext(), context.getFinalDatabaseContext());
    }
}
