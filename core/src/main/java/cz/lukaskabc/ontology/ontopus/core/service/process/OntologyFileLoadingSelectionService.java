package cz.lukaskabc.ontology.ontopus.core.service.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.FileLoadingService;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(ImportProcessServiceOrder.DATA_LOADING_SELECTION_SERVICE)
public class OntologyFileLoadingSelectionService implements OrderedImportPipelineService<FileLoadingService> {

    private static JsonForm makeForm(List<FileLoadingService> services, ObjectMapper objectMapper) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        ArrayNode items = schema.putArray("oneOf");
        for (int i = 0; i < services.size(); i++) {
            items.addObject()
                    .put("type", "object")
                    .putObject("properties")
                    .putObject("service")
                    .put("value", i)
                    .put("title", services.get(i).getServiceName());
        }

        ObjectNode uiSchema = objectMapper.createObjectNode();
        uiSchema.put("ui:field", "showAll"); // TODO specify UI field that will display all the buttons
        uiSchema.put("ui:fieldReplacesAnyOrOneOf", true);

        return new JsonForm(schema, uiSchema);
    }

    private final List<FileLoadingService> fileLoadingServices;

    private final JsonForm jsonForm;

    public OntologyFileLoadingSelectionService(
            List<FileLoadingService> fileLoadingServices, ObjectMapper objectMapper) {
        this.fileLoadingServices = fileLoadingServices;
        this.jsonForm = makeForm(fileLoadingServices, objectMapper);
        if (fileLoadingServices.isEmpty()) {
            throw new IllegalStateException("No file loading service found for ontology import!"); // TODO exception
        }
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return jsonForm;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OrderedImportPipelineService.OntologyFileLoadingSelectionService.name";
    }

    @Override
    public Result<FileLoadingService> handleSubmit(FormResult formResult, ImportProcessContext context) {
        return null;
    }
}
