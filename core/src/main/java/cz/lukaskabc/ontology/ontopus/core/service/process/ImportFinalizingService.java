package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.ServiceAwareFormResult;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core.service.OntologyFileService;
import cz.lukaskabc.ontology.ontopus.core.util.ImportContextUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.UploadedFile;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.CatalogRepository;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionSeriesRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.ResourceInContextMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ImportFinalizingService {
    private static final Logger log = LogManager.getLogger(ImportFinalizingService.class);

    private static @NonNull Map<String, UploadedFile> getUploadedFileMap(FormResult result) {
        // Map<String, UploadedFile> reusableFiles =
        // new HashMap<>(result.submittedFiles().size());
        //
        // for (List<MultipartFile> files : result.submittedFiles().values()) {
        // for (MultipartFile file : files) {
        // String fileName = file.getOriginalFilename();
        // if (fileName == null) {
        // fileName = file.getName();
        // }
        // // TODO review path here
        // UploadedFile uploadedFile = new UploadedFile(fileName,
        // Path.of(file.getName()));
        // reusableFiles.put(fileName, uploadedFile);
        // }
        // }

        // return reusableFiles;
        return Map.of(); // TODO whole ImportFinalizingService needs to be redone
    }

    private final ResourceInContextMappingService resourceInContextMappingService;

    private final TimeProvider timeProvider;

    private final ObjectMapper objectMapper;

    private final VersionSeriesRepository versionSeriesRepository;
    private final VersionArtifactRepository versionArtifactRepository;

    private final EntityManager em;

    private final OntologyFileService fileService;

    private final CatalogRepository catalogRepository;

    public ImportFinalizingService(
            TimeProvider timeProvider,
            EntityManager em,
            OntologyFileService fileService,
            ObjectMapper objectMapper,
            VersionSeriesRepository versionSeriesRepository,
            VersionArtifactRepository versionArtifactRepository,
            CatalogRepository catalogRepository,
            ResourceInContextMappingService resourceInContextMappingService) {
        this.timeProvider = timeProvider;
        this.em = em;

        this.fileService = fileService;
        this.objectMapper = objectMapper;
        this.versionSeriesRepository = versionSeriesRepository;
        this.versionArtifactRepository = versionArtifactRepository;
        this.catalogRepository = catalogRepository;
        this.resourceInContextMappingService = resourceInContextMappingService;
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
    public void finalize(ImportProcessContext context) {
        // TODO: There should be some validation ensuring that the constructed artifacts
        // are valid
        // and possible errors should be propagated to the user
        // This can be handled by another previous service and validation here should be
        // only the last resort
        // if it fails, the import failed
        Path artifactImportFolder = fileService.createArtifactImportFolder();

        persistFiles(context, artifactImportFolder);
        serializeContext(context, artifactImportFolder);
        updateVersionSeries(context);
        final GraphURI graphURI = persistDatabaseContext(context);
        resourceInContextMappingService.mapResourcesFromContext(graphURI);

        final VersionSeries series = context.getVersionSeries();
        final VersionArtifact artifact = context.getVersionArtifact();

        versionSeriesRepository.update(series);
        versionArtifactRepository.delete(artifact);
        versionArtifactRepository.update(artifact);
        // TODO: what if the artifact already exists?

        updateCatalog(series);

        // TODO delete files from previous import process
        // TODO: create a mechanism that will clear files for old artifacts
        // perhaps at the end of saving a new artifact, delete the old files (after
        // successful transaction)

        // TODO: compile serializable import context
        // backup files for reuse
        // publish event?
    }

    /**
     * Moves the temporary graph from the context to the target ontology graph specified by the identifier of the
     * version artifact. All data from the target graph are dropped before the move.
     */
    private GraphURI persistDatabaseContext(ImportProcessContext context) {
        VersionArtifactURI ontologyGraph =
                Objects.requireNonNull(context.getVersionArtifact().getIdentifier());

        try {
            // according to SPARQL specification, the destination graph is removed before
            // insertion
            // https://www.w3.org/TR/sparql11-update/#move
            this.em
                    .createNativeQuery("MOVE GRAPH ?source TO ?target")
                    .setParameter("source", context.getDatabaseContext().toURI())
                    .setParameter("target", ontologyGraph.toURI())
                    .executeUpdate();

            return new GraphURI(ontologyGraph.toURI());
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /** Copies the temporary directory and all uploaded files to a persistent one */
    private void persistFiles(ImportProcessContext context, Path destinationFolder) {
        Path sourcePath = context.getTempFolder();

        try {
            Files.createDirectories(destinationFolder);
            // not moving to allow database save to file and try again
            FileUtils.copyDirectory(sourcePath.toFile(), destinationFolder.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }

        // TODO copy all uploaded files?
        // context.getProcessedResults()
        // .forEach(result -> result.submittedFiles().values().forEach(file -> {
        // try {
        //
        // file.transferTo(targetPath.resolve(file.getName()));
        // } catch (IOException e) {
        // throw new RuntimeException(e); // TODO: exception
        // }
        // }));
    }

    private void serializeContext(ImportProcessContext context, Path artifactImportFolder) {
        final SerializableImportProcessContext serializedContext = new SerializableImportProcessContext();
        serializedContext.setVersionSeriesIdentifier(
                context.getVersionSeries().getIdentifier().toURI());
        serializedContext.setFilesDirectory(artifactImportFolder.toString());

        final int servicesCount = context.getProcessedServices().size();
        final int resultsCount = context.getProcessedResults().size();
        assert servicesCount >= resultsCount;

        final List<String> serviceIds = new ArrayList<>(servicesCount);
        final HashMap<String, FormResult> serviceToFormResultMap = new HashMap<>(resultsCount);

        serializedContext.setServicesList(serviceIds);
        serializedContext.setServiceToFormResultMap(serviceToFormResultMap);

        int resultIndex = 0;
        for (int serviceIndex = 0; serviceIndex < servicesCount; serviceIndex++) {
            final ImportProcessingService<?> service =
                    context.getProcessedServices().get(serviceIndex);
            final String serviceId = ImportContextUtils.getIndexedServiceIdentifier(service, serviceIndex);
            serviceIds.add(serviceId);
            final ServiceAwareFormResult result = context.getProcessedResults().get(resultIndex);
            if (result.service() == service) {
                // Map<String, UploadedFile> reusableFiles =
                // getUploadedFileMap(result.formResult());
                // Map<String, String> serializedFormData =
                // serializeFormData(result.formResult().formData());
                assert !serviceToFormResultMap.containsKey(serviceId);

                serviceToFormResultMap.put(serviceId, result.formResult());
                resultIndex++;
            }
        }

        context.getVersionSeries().setSerializableImportProcessContext(serializedContext);

        log.warn(() -> objectMapper.writeValueAsString(serializedContext));
    }

    private Map<String, String> serializeFormData(Map<String, JsonNode> formData) {
        final Map<String, String> result = new HashMap<>(formData.size());
        for (final Map.Entry<String, JsonNode> entry : formData.entrySet()) {
            final String key = entry.getKey();
            final JsonNode value = entry.getValue();
            result.put(key, objectMapper.writeValueAsString(value));
        }
        return result;
    }

    private void updateCatalog(VersionSeries series) {
        final OntopusCatalog catalog = catalogRepository.findRequired();
        catalog.addVersionSeries(series.getIdentifier());
        catalogRepository.update(catalog);
    }

    private void updateVersionSeries(ImportProcessContext context) {
        final VersionArtifact artifact = context.getVersionArtifact();
        final VersionSeries series = context.getVersionSeries();

        final Instant timestamp = timeProvider.getInstant();
        if (series.getLast() != null) {
            artifact.setPreviousVersion(series.getLast());
        }
        artifact.setReleaseDate(timestamp);
        artifact.setModifiedDate(timestamp);
        artifact.setSeries(series.getIdentifier().toURI());
        series.getMembers().add(artifact.getIdentifier());
        series.setLast(artifact.getIdentifier());
        if (series.getFirst() == null) {
            series.setFirst(artifact.getIdentifier());
        }
        series.setModifiedDate(timestamp);
        if (series.getReleaseDate() == null) {
            series.setReleaseDate(timestamp);
        }

        series.setVersion(timeProvider.getCurrentDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
