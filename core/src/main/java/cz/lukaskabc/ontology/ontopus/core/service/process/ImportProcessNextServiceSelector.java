package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

public abstract class ImportProcessNextServiceSelector<S extends ImportProcessingService<?>>
        implements ImportProcessingService<S> {

    protected final List<S> services;
    protected final ObjectMapper objectMapper;
    protected final JsonForm jsonForm;
    protected final boolean showDescription;

    public ImportProcessNextServiceSelector(List<S> services, boolean showDescription, ObjectMapper objectMapper) {
        if (services.isEmpty()) {
            throw new IllegalStateException("No services found for service selection!"); // TODO exception
        }
        this.services = services;
        this.showDescription = showDescription;
        this.objectMapper = objectMapper;
        this.jsonForm = makeForm();
    }

    @Override
    public @NonNull JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return makeForm().withFormData(previousFormData);
    }

    @Override
    public S handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        JsonNode serviceIndex = formResult.formData().getOrDefault("service", objectMapper.nullNode());
        if (serviceIndex.isNumber()) {
            int index = serviceIndex.asInt();
            if (index >= 0 && index < services.size()) {
                return services.get(index);
            }
        }
        throw new JsonFormSubmitException("Invalid service index!"); // TODO exception and passing it to the FE
    }

    protected JsonForm makeForm() {
        ObjectNode schema = objectMapper.createObjectNode();
        ObjectNode uiSchema = objectMapper.createObjectNode();

        schema.put("type", "object");
        ObjectNode properties = schema.putObject("properties");
        ObjectNode service = properties
                .putObject("service")
                .put("type", "number")
                .put("title", getServiceName())
                .put("default", 0)
                .put("description", getServiceDescription());

        schema.putArray("required").add("service");
        ArrayNode items = service.putArray("oneOf");
        ArrayNode allOf = schema.putArray("allOf");
        for (int i = 0; i < services.size(); i++) {
            final ImportProcessingService<?> item = services.get(i);
            items.addObject()
                    .put("const", i)
                    .put("title", item.getServiceName())
                    .put("description", item.getServiceDescription());

            if (!showDescription) {
                continue;
            }
            // option description below
            ObjectNode condition = allOf.addObject();
            condition
                    .putObject("if")
                    .putObject("properties")
                    .putObject("service")
                    .put("const", i);
            condition
                    .putObject("then")
                    .putObject("properties")
                    .putObject(item.getServiceName())
                    .put("type", "null")
                    .put("description", item.getServiceDescription());
            uiSchema.putObject(item.getServiceName()).put("ui:field", "typographyField");
        }

        return new JsonForm(schema, uiSchema, null);
    }
}
