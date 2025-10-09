package cz.lukaskabc.ontology.ontopus.core.service.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyLoadingService;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class FileUploadOntologyLoadingService implements OntologyLoadingService {
    private final JsonForm jsonForm;
    private final ObjectMapper objectMapper;

    public FileUploadOntologyLoadingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonForm = makeForm();
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return jsonForm;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OntologyLoadingService.FileUploadOntologyLoadingService.title";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
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
