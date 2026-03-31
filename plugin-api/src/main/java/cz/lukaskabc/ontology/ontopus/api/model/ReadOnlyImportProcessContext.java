package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Read-only view of the {@link ImportProcessContext}. The retrieved object should not be modified, but they are not
 * guaranteed to be immutable.
 */
public interface ReadOnlyImportProcessContext {
    <T> Optional<T> getAdditionalProperty(Object key, Class<T> type);

    Set<ContextToControllerMapping> getControllerMappings();

    @Nullable Path getOntologyFilePath();

    List<ImportProcessingService<?>> getPendingServicesStack();

    List<ServiceAwareFormResult> getProcessedResults();

    List<ImportProcessingService<?>> getProcessedServices();

    Path getTempFolder();

    TemporaryContextURI getTemporaryDatabaseContext();

    VersionArtifact getVersionArtifact();

    VersionSeries getVersionSeries();
}
