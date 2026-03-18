package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.CatalogRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionArtifactService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ImportFinalizationService {
    private static final Logger log = LogManager.getLogger(ImportFinalizationService.class);

    private final List<ImportFinalizingService> importFinalizingServices;
    private final VersionSeriesService versionSeriesService;
    private final VersionArtifactService versionArtifactService;
    private final CatalogRepository catalogRepository;

    public ImportFinalizationService(
            List<ImportFinalizingService> importFinalizingServices,
            VersionSeriesService versionSeriesService,
            VersionArtifactService versionArtifactService,
            CatalogRepository catalogRepository) {
        this.importFinalizingServices = importFinalizingServices;
        this.versionSeriesService = versionSeriesService;
        this.versionArtifactService = versionArtifactService;
        this.catalogRepository = catalogRepository;

        if (importFinalizingServices.isEmpty()) {
            throw new IllegalStateException("No ImportFinalizingService implementations found.");
        }
    }

    /**
     *
     *
     * <ol>
     *   <li>Persist files
     *   <li>Persist database context
     *   <li>Serialize import process to the version series
     *   <li>Update version series with the new artifact
     *   <li>Persist artifacts (which will also validate them), they will be persisted in respective internal contexts
     *   <li>Delete files from previous import process
     * </ol>
     *
     * @param context the process context to finalize
     */
    @Transactional
    public void finalizeImport(ImportProcessContext context) {
        // TODO: There should be some validation ensuring that the constructed artifacts
        // are valid
        // and possible errors should be propagated to the user
        // This can be handled by another previous service and validation here should be
        // only the last resort
        // if it fails, the import failed
        log.info(
                "Finalizing import process for version series: {}",
                context.getVersionSeries().getOntologyURI());

        importFinalizingServices.forEach(service -> service.finalizeImport(context));

        final VersionSeries series = context.getVersionSeries();
        final VersionArtifact artifact = context.getVersionArtifact();

        versionSeriesService.save(series);

        versionArtifactService.delete(artifact);
        versionArtifactService.persist(artifact);

        updateCatalog(series);

        // TODO: publish event?
    }

    private void updateCatalog(VersionSeries series) {
        final OntopusCatalog catalog = catalogRepository.findRequired();
        Objects.requireNonNull(series.getIdentifier(), "Version series identifier must not be null");
        catalog.addVersionSeries(series.getIdentifier());
        catalogRepository.update(catalog);
    }
}
