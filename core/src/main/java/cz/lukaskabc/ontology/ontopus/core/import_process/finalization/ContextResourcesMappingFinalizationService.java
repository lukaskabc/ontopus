package cz.lukaskabc.ontology.ontopus.core.import_process.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.ResourceInContextMappingService;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.stereotype.Service;

/**
 * Maps all resources from the destination graph to the context.
 *
 * @see DatabaseContextPersistFinalizationService
 * @see ResourceInContextMappingService#mapResourcesFromContext(GraphURI)
 */
@Service
@Order(FinalizationServiceOrder.CONTEXT_RESOURCES_MAPPING)
public class ContextResourcesMappingFinalizationService implements ImportFinalizingService {
    private final ResourceInContextMappingService resourceInContextMappingService;

    public ContextResourcesMappingFinalizationService(ResourceInContextMappingService resourceInContextMappingService) {
        this.resourceInContextMappingService = resourceInContextMappingService;
    }

    @Override
    public void finalizeImport(ImportProcessContext context) {
        final GraphURI graphUri = context.getFinalDatabaseContext();
        resourceInContextMappingService.mapResourcesFromContext(graphUri);
    }
}
