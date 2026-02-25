package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;

import java.nio.file.Path;
import java.util.List;

/**
 * Read-only view of the {@link ImportProcessContext}. The retrieved object should not be modified, but they are not
 * guaranteed to be immutable.
 */
public interface ReadOnlyImportProcessContext {
    TemporaryContextURI getDatabaseContext();

    List<ImportProcessingService<?>> getPendingServicesStack();

    List<ServiceAwareFormResult> getProcessedResults();

    List<ImportProcessingService<?>> getProcessedServices();

    Path getTempFolder();

    VersionArtifact getVersionArtifact();

    VersionSeries getVersionSeries();
}
