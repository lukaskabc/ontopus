package cz.lukaskabc.ontology.ontopus.plugin.file;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyLoadingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

/**
 * An {@link OntologyLoadingService} capable of accepting upload of local files and importing them into the database.
 */
@Service
public class FileUploadOntologyLoadingService implements OntologyLoadingService {
    private final FileUploadProcessingService fileUploadService;

    public FileUploadOntologyLoadingService(FileUploadProcessingService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return this.fileUploadService.getJsonForm(context, previousFormData);
    }

    @Override
    public String getServiceName() {
        return "ontopus.plugin.file.FileUploadProcessingService.title";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        this.fileUploadService.handleSubmit(formResult, context);
        return null;
    }
}
