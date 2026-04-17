package cz.lukaskabc.ontology.ontopus.plugin.file;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyLoadingService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.UploadedFile;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.Map;

/**
 * {@link OntologyLoadingService} allowing to upload a local files to server.
 *
 * @implSpec Must not be pushed to the context stack on its own. Rather, should be wrapped by another service.
 */
@Service
public class FileUploadProcessingService implements ImportProcessingService<Map<String, UploadedFile>> {
    private static JsonFormSubmitException noFileException(String internalMessage) {
        return JsonFormSubmitException.builder()
                .errorType(Vocabulary.u_i_no_file)
                .internalMessage(internalMessage)
                .titleMessageCode("ontopus.core.error.missingFile")
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .detailMessageCode("ontopus.core.error.noFileUploaded")
                .build();
    }

    private final JsonForm jsonForm;

    private final ObjectMapper objectMapper;

    public FileUploadProcessingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonForm = makeForm();
    }

    @Override
    public void afterStackPush(ImportProcessContext context) {
        throw new AssertionError("FileUploadProcessingService must not be pushed to stack");
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return jsonForm; // not using previous data, files cannot be reused
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public Map<String, UploadedFile> handleSubmit(FormResult formResult, ImportProcessContext context)
            throws JsonFormSubmitException {
        // there is not much to do, the files were uploaded and placed in to the context
        // temp directory
        // we can just validate that they exist
        if (formResult.uploadedFiles().isEmpty()) {
            throw noFileException("No file uploaded");
        }

        for (UploadedFile file : formResult.uploadedFiles().values()) {
            File ioFile = file.fsPath().toFile();
            if (!ioFile.isFile()) {
                throw noFileException("The uploaded file was not found in file system: " + ioFile.getAbsolutePath());
            }
        }

        return formResult.uploadedFiles();
    }

    protected JsonForm makeForm() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.put("$translationRoot", "ontopus.plugin.file.FileUploadProcessingService");
        schema.putArray("required").add("files");
        schema.putObject("properties").putObject("files").put("type", "array").put("minItems", 1);

        ObjectNode uiSchema = objectMapper.createObjectNode();
        uiSchema.putObject("files").put("ui:field", "fileField").put("multiple", true);
        return new JsonForm(schema, uiSchema, null);
    }
}
