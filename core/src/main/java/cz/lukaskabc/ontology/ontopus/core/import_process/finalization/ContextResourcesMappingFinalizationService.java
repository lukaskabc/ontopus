package cz.lukaskabc.ontology.ontopus.core.import_process.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.ResourceInContextMappingService;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Maps all resources from the destination graph to the context.
 *
 * @see DatabaseContextPersistFinalizationService
 * @see ResourceInContextMappingService#remapResourcesFromContext(GraphURI)
 * @see ResourceInContextMappingService#remapUnmappedResourcesFromContext(GraphURI)
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
        final VersionArtifactURI artifactURI = context.getVersionArtifact().getIdentifier();
        final boolean isLatest = Objects.equals(context.getVersionSeries().getLast(), artifactURI);

        final GraphURI graphUri = context.getFinalDatabaseContext();

        if (isLatest) {
            // If the version is latest, all resources should be remapped to this version
            resourceInContextMappingService.remapResourcesFromContext(graphUri);
        } else {
            // If the version is not the latest, only missing resources should be mapped to
            // this version
            resourceInContextMappingService.remapUnmappedResourcesFromContext(graphUri);
        }
    }
}
