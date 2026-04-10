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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ImportProcessNextServiceSelector<S extends ImportProcessingService<?>>
        implements ImportProcessingService<S> {

    protected final List<S> services;
    protected final ObjectMapper objectMapper;

    public ImportProcessNextServiceSelector(List<S> services, ObjectMapper objectMapper) {
        if (services.isEmpty()) {
            throw new IllegalStateException("No services found for service selection!"); // TODO exception
        }
        this.services = services;
        this.objectMapper = objectMapper;
    }

    @Override
    public @NonNull JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return makeForm(context).withFormData(previousFormData);
    }

    @Override
    public S handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        JsonNode serviceIndex = formResult.formData().getOrDefault("service", objectMapper.nullNode());
        if (serviceIndex.isNumber()) {
            int index = serviceIndex.asInt();
            if (index >= 0 && index < services.size()) {
                final S service = services.get(index);
                if (showsWrappedServiceForm() && service.getJsonForm(context, null) != null) {
                    submitInnerForm(service, formResult, context);
                }
                return service;
            }
        }
        throw new JsonFormSubmitException("Invalid service index!"); // TODO exception and passing it to the FE
    }

    protected JsonForm makeForm(ReadOnlyImportProcessContext context) {
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

            // option description below
            ObjectNode condition = allOf.addObject();
            condition
                    .putObject("if")
                    .putObject("properties")
                    .putObject("service")
                    .put("const", i);
            final ObjectNode thenProperties = condition.putObject("then").putObject("properties");

            final JsonForm innerForm = item.getJsonForm(context, null);
            if (showsWrappedServiceForm() && innerForm != null) {
                thenProperties.set(item.getServiceName(), innerForm.getJsonSchema());
                if (innerForm.getUiSchema() != null) {
                    uiSchema.set(item.getServiceName(), innerForm.getUiSchema());
                }
            } else {
                thenProperties
                        .putObject(item.getServiceName())
                        .put("type", "null")
                        .put("description", item.getServiceDescription());
                uiSchema.putObject(item.getServiceName()).put("ui:field", "typographyField");
            }
        }

        return new JsonForm(schema, uiSchema, null);
    }

    /**
     * Whether the service renders nested form based on the selection
     *
     * @return true if the nested form is included and its result submitted to the wrapped service, false otherwise.
     */
    public boolean showsWrappedServiceForm() {
        return true;
    }

    protected <T> T submitInnerForm(
            ImportProcessingService<? extends T> service, FormResult formResult, ImportProcessContext context)
            throws JsonFormSubmitException {
        final JsonNode selectionResult = formResult.jsonFormData(objectMapper);
        final String property = service.getServiceName();
        if (!selectionResult.hasNonNull(property)) {
            throw new JsonFormSubmitException("No inner form found!");
        }

        ObjectNode innerData = selectionResult.get(property).asObject();
        Map<String, JsonNode> innerFormData = new HashMap<>(innerData.size());
        innerData.forEachEntry(innerFormData::put);
        return service.handleSubmit(new FormResult(innerFormData, formResult.uploadedFiles()), context);
    }
}
