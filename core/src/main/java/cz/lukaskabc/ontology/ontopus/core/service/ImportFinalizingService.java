package cz.lukaskabc.ontology.ontopus.core.service;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core.model.util.SerializableImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core.model.util.UploadedFile;
import cz.lukaskabc.ontology.ontopus.core.persistence.dao.VersionSeriesDao;
import cz.lukaskabc.ontology.ontopus.core.util.ImportContextUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportFinalizingService {
    private static @NonNull Map<String, UploadedFile> getUploadedFileMap(FormResult result) {
        Map<String, UploadedFile> reusableFiles =
                new HashMap<>(result.submittedFiles().size());

        for (List<MultipartFile> files : result.submittedFiles().values()) {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                if (fileName == null) {
                    fileName = file.getName();
                }
                // TODO review path here
                UploadedFile uploadedFile = new UploadedFile(fileName, Path.of(file.getName()));
                reusableFiles.put(fileName, uploadedFile);
            }
        }

        return reusableFiles;
    }
    /** Copies the temporary directory and all uploaded files to a persistent one an */
    private static void persistFiles(ImportProcessContext context, Path targetPath) {
        Path sourcePath = context.getTempFolder();

        try {
            Files.createDirectories(targetPath);
            // not moving to allow database save to file and try again
            FileUtils.copyDirectory(sourcePath.toFile(), targetPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }

        // copy all uploaded files
        // context.getProcessedResults()
        // .forEach(result -> result.submittedFiles().values().forEach(file -> {
        // try {
        //
        // file.transferTo(targetPath.resolve(file.getName()));
        // } catch (IOException e) {
        // throw new RuntimeException(e); // TODO: exception
        // }
        // }));

        // TODO: create a mechanism that will clear files for old artifacts
        // perhaps at the end of saving a new artifact, delete the old files (after
        // successful transaction)
    }

    private static SerializableImportProcessContext persistImportContext(ImportProcessContext context, Path filesPath) {
        final SerializableImportProcessContext persistentContext = new SerializableImportProcessContext();
        persistentContext.setFilesDirectory(filesPath.toString());
        final List<String> services =
                new ArrayList<>(context.getProcessedServices().size());
        int serviceIndex = 0;
        for (final ImportProcessingService<?> service : context.getProcessedServices()) {
            services.add(ImportContextUtils.getIndexedServiceIdentifier(service, serviceIndex));
        }
        persistentContext.setServicesList(services);

        final List<SerializableImportProcessContext.ReusableFormResult> formResults =
                new ArrayList<>(context.getProcessedResults().size());
        for (final FormResult result : context.getProcessedResults()) {
            Map<String, UploadedFile> reusableFiles = getUploadedFileMap(result);
            // formResults.add(new
            // SerializableImportProcessContext.ReusableFormResult(result.formData(),
            // reusableFiles));
        }
        persistentContext.setFormResults(formResults);

        return persistentContext;
    }

    private final EntityManager em;

    private final VersionSeriesDao ontologyArtifactVersionSeriesDao;

    private final OntologyFileService fileService;

    public ImportFinalizingService(
            EntityManager em, VersionSeriesDao ontologyArtifactVersionSeriesDao, OntologyFileService fileService) {
        this.em = em;

        this.ontologyArtifactVersionSeriesDao = ontologyArtifactVersionSeriesDao;
        this.fileService = fileService;
    }

    @Transactional
    public void finalize(ImportProcessContext context) {
        context.getVersionArtifact();

        // final URI ontologyGraph =
        // context.getOntologyVersionArtifact().getCurrentVersion();
        // Path artifactImportFolder = fileService.createArtifactImportFolder();
        // persistDatabaseContext(context, ontologyGraph);
        // persistFiles(context, artifactImportFolder);
        //
        // SerializableImportProcessContext persistentContext =
        // persistImportContext(context, artifactImportFolder);
        //
        // OntologyArtifactVersionSeries series =
        // context.getOntologyArtifactVersionSeries();
        //
        // series.setLatestArtifact(context.getOntologyVersionArtifact());
        // series.getOntologyArtifacts().add(context.getOntologyVersionArtifact());
        // series.setSerializableImportProcessContext(persistentContext);
        //
        // ontologyArtifactVersionSeriesDao.persist(series);

        // TODO: compile serializable import context
        // backup files for reuse
        // publish event?

    }

    /**
     * Moves the temporary graph from the context to the target ontology graph. All data from the target graph are
     * dropped.
     *
     * @param ontologyGraph The target database graph
     */
    private void persistDatabaseContext(ImportProcessContext context, URI ontologyGraph) {
        this.em
                .createNativeQuery("DROP GRAPH ?target")
                .setParameter("target", ontologyGraph)
                .executeUpdate();
        this.em
                .createNativeQuery("MOVE GRAPH ?source TO ?target")
                .setParameter("source", context.getDatabaseContext())
                .setParameter("target", ontologyGraph)
                .executeUpdate();
    }
}
