package cz.lukaskabc.ontology.ontopus.plugin.file;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyLoadingService;
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
        return jsonForm; // not using previous data
    }

    @Override
    public String getServiceName() {
        return "ontopus.plugin.file.serviceName";
    }

    @Override
    public Map<String, UploadedFile> handleSubmit(FormResult formResult, ImportProcessContext context) {
        // there is not much to do, the files were uploaded and placed in to the context
        // temp directory
        // we can just validate that they exist

        for (UploadedFile file : formResult.uploadedFiles().values()) {
            File ioFile = file.fsPath().toFile();
            if (!ioFile.isFile()) {
                throw new UploadedFileNotFoundException(
                        "The uploaded file was not found in file system: " + ioFile.getAbsolutePath());
            }
        }

        return formResult.uploadedFiles();
    }

    protected JsonForm makeForm() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.put("title", getServiceName());
        schema.putArray("required").add("files");
        schema.putObject("properties").putObject("files").put("type", "array");

        ObjectNode uiSchema = objectMapper.createObjectNode();
        uiSchema.putObject("files").put("ui:field", "fileField").put("multiple", true);
        return new JsonForm(schema, uiSchema, null);
    }
}
