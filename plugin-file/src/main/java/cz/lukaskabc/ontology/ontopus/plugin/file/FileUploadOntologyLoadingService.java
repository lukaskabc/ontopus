package cz.lukaskabc.ontology.ontopus.plugin.file;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.core.FileToDatabaseImportingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyLoadingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.UploadedFile;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An {@link OntologyLoadingService} capable of accepting upload of local files and importing them into the database.
 */
@Service
public class FileUploadOntologyLoadingService implements OntologyLoadingService {
    private final FileUploadProcessingService fileUploadService;
    private final FileToDatabaseImportingService fileImportingService;

    public FileUploadOntologyLoadingService(
            FileUploadProcessingService fileUploadService, FileToDatabaseImportingService fileImportingService) {
        this.fileUploadService = fileUploadService;
        this.fileImportingService = fileImportingService;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return this.fileUploadService.getJsonForm(context, previousFormData);
    }

    @Override
    public String getServiceName() {
        return "ontopus.plugin.file.serviceName";
    }

    /**
     * Imports all the files into the database.
     *
     * @param uploadedFiles the uploaded files
     * @param context the import process context
     */
    private void handleFiles(Map<String, UploadedFile> uploadedFiles, ImportProcessContext context) throws IOException {
        List<File> files = new ArrayList<>(uploadedFiles.values().stream()
                .map(UploadedFile::fsPath)
                .map(Path::toFile)
                .toList());
        fileImportingService.importFiles(files, context);
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        Map<String, UploadedFile> uploadedFiles = this.fileUploadService.handleSubmit(formResult, context);
        try {
            handleFiles(uploadedFiles, context);
        } catch (IOException e) {
            throw new FileImportingException("Unable to import uploaded files to database.", e);
        }
        return null;
    }
}
