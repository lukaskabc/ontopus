package cz.lukaskabc.ontology.ontopus.plugin.file;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReusableFile;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyLoadingService;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/** {@link OntologyLoadingService} allowing to upload a local files to server. */
@Service
public class FileUploadProcessingService implements ImportProcessingService<Map<String, ReusableFile>> {
    private final JsonForm jsonForm;
    private final ObjectMapper objectMapper;

    public FileUploadProcessingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonForm = makeForm();
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return jsonForm;
    }

    @Override
    public String getServiceName() {
        return "ontopus.plugin.file_upload.serviceName";
    }

    @Override
    public Map<String, ReusableFile> handleSubmit(FormResult formResult, ImportProcessContext context) {
        // there is not much to do, the files were uploaded and placed in to the context
        // temp directory
        // we can just validate that they exist

        for (ReusableFile file : formResult.reusableFiles().values()) {
            if (!file.getFile().isFile()) {
                throw new UploadedFileNotFoundException("The uploaded file was not found in file system: "
                        + file.getFile().getAbsolutePath());
            }
        }

        return formResult.reusableFiles();
    }

    protected JsonForm makeForm() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "array");
        schema.put("title", getServiceName());

        ObjectNode uiSchema = objectMapper.createObjectNode();
        uiSchema.put("ui:field", "reusableFileField").put("multiple", true).put("directory", true);
        return new JsonForm(schema, uiSchema, null);
    }
}
