package cz.lukaskabc.ontology.ontopus.plugin.file;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReusableFile;
import cz.lukaskabc.ontology.ontopus.api.service.DataFileImportingService;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyLoadingService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * An {@link OntologyLoadingService} capable of accepting upload of local files and importing them into the database.
 */
@Service
public class FileUploadOntologyLoadingService implements OntologyLoadingService {
    private final FileUploadProcessingService fileUploadService;
    private final List<DataFileImportingService> dataFileImportingServices;

    public FileUploadOntologyLoadingService(
            FileUploadProcessingService fileUploadService, List<DataFileImportingService> dataFileImportingServices) {
        this.fileUploadService = fileUploadService;
        this.dataFileImportingServices = dataFileImportingServices;
        if (dataFileImportingServices.isEmpty()) {
            throw new IllegalArgumentException("No data file importing service found!");
        }
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return this.fileUploadService.getJsonForm();
    }

    @Override
    public String getServiceName() {
        return "ontopus.plugin.file_upload.serviceName";
    }

    /**
     * Imports all the files into the database.
     *
     * @param uploadedFiles the uploaded files
     * @param context the import process context
     */
    private void handleFiles(Map<String, ReusableFile> uploadedFiles, ImportProcessContext context) throws IOException {
        List<File> files = new ArrayList<>(
                uploadedFiles.values().stream().map(ReusableFile::getFile).toList());
        List<File> toImport = new ArrayList<>(files.size());
        for (DataFileImportingService importingService : dataFileImportingServices) {
            for (File file : files) {
                if (importingService.supports(file)) {
                    toImport.add(file);
                }
            }
            if (!toImport.isEmpty()) {
                importingService.importFiles(toImport, context);
                files.removeAll(toImport);
                toImport.clear();
            }
        }
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        Map<String, ReusableFile> uploadedFiles = this.fileUploadService.handleSubmit(formResult, context);
        try {
            handleFiles(uploadedFiles, context);
        } catch (IOException e) {
            throw new FileImportingException("Unable to import uploaded files to database.", e);
        }
        return null;
    }
}
