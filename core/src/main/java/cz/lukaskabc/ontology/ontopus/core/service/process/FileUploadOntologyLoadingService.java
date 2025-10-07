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
        return null;
    }

    protected JsonForm makeForm() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.put("title", getServiceName());
        ObjectNode properties = schema.putObject("properties");
        ObjectNode files = properties.putObject("files");
        files.put("type", "string");
        files.put("format", "data-url");
        files.put("multiple", true);

        ObjectNode uiSchema = objectMapper.createObjectNode();
        return new JsonForm(schema, uiSchema, null);
    }
}
