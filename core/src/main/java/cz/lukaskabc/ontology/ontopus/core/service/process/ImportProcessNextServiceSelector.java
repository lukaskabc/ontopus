package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import java.util.List;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

public abstract class ImportProcessNextServiceSelector<S extends ImportProcessingService<?>>
        implements ImportProcessingService<S> {

    protected final List<S> services;
    protected final ObjectMapper objectMapper;
    protected final JsonForm jsonForm;

    public ImportProcessNextServiceSelector(List<S> services, ObjectMapper objectMapper) {
        if (services.isEmpty()) {
            throw new IllegalStateException("No services found for service selection!"); // TODO exception
        }
        this.services = services;
        this.objectMapper = objectMapper;
        this.jsonForm = makeForm();
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return jsonForm;
    }

    @Override
    public S handleSubmit(FormResult formResult, ImportProcessContext context) {
        JsonNode serviceIndex = formResult.formData().getOrDefault("service", objectMapper.nullNode());
        if (serviceIndex.isNumber()) {
            int index = serviceIndex.asInt();
            if (index >= 0 && index < services.size()) {
                return services.get(index);
            }
        }
        throw new IllegalArgumentException("Invalid service index!"); // TODO exception and passing it to the FE
    }

    protected JsonForm makeForm() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        ObjectNode properties = schema.putObject("properties");
        ObjectNode service =
                properties.putObject("service").put("type", "number").put("title", getServiceName());

        schema.putArray("required").add("service");
        ArrayNode items = service.putArray("oneOf");
        for (int i = 0; i < services.size(); i++) {
            items.addObject().put("const", i).put("title", services.get(i).getServiceName());
        }

        ObjectNode uiSchema = objectMapper.createObjectNode();
        return new JsonForm(schema, uiSchema, null);
    }
}
