package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyPublishingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.nio.file.Path;

@Order(Ordered.LOWEST_PRECEDENCE)
@Service
public class WidocoPublishingService implements OntologyPublishingService, OrderedImportPipelineService<Void> {
    private static JsonForm makeForm(ObjectMapper mapper) {
        final ObjectNode schema = mapper.createObjectNode();
        final ObjectNode uiSchema = mapper.createObjectNode();
        schema.put("type", "object");
        schema.put("$translationRoot", "ontopus.plugin.widoco.service.WidocoPublishingService");
        final ObjectNode properties = schema.putObject("properties");

        for (WidocoArguments.Argument arg : WidocoArguments.Argument.values()) {
            properties.putObject(arg.name()).put("type", arg.schemaType());
        }

        ObjectNode autocompleteWidget = mapper.createObjectNode()
                .put("ui:widget", "autocompleteWidget")
                .putObject("ui:options")
                .put("freeSolo", false)
                .put("openOnFocus", true)
                .put("disableClearable", true)
                .put("autoHighlight", true);

        uiSchema.set(WidocoArguments.Argument.CONF_FILE.name(), autocompleteWidget);
        uiSchema.set(WidocoArguments.Argument.ONT_FILE.name(), autocompleteWidget);

        return new JsonForm(schema, uiSchema, null);
    }

    private final ObjectMapper mapper;

    private final JsonForm jsonForm;

    public WidocoPublishingService(ObjectMapper mapper) {
        this.mapper = mapper;
        this.jsonForm = makeForm(mapper);
    }
    /**
     * Provides a JSON form which will be shown to the user.
     *
     * @param context The import process context. Contents should not be modified.
     * @param previousFormData The data submitted in the previous import process of the ontology version series.
     * @return Form with JSON scheme and an optional UI Scheme
     * @implSpec The method can be called multiple times during the process execution, the result should be cached when
     *     possible.
     */
    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final JsonNode schema = jsonForm.getJsonSchema().asObject();
        final JsonNode uiSchema = jsonForm.getUiSchema();

        ArrayNode examples = mapper.createArrayNode();

        FileUtils.listRecursively(context.getTempFolder())
                .map(context.getTempFolder()::relativize)
                .map(Path::toString)
                .forEach(examples::add);

        final JsonNode properties = schema.get("properties");
        properties.get(WidocoArguments.Argument.CONF_FILE.name()).asObject().set("examples", examples);
        properties.get(WidocoArguments.Argument.ONT_FILE.name()).asObject().set("examples", examples);

        return new JsonForm(schema, uiSchema, null);
    }

    /**
     * Provides information about actions of this service. How the ontology will be published (e.g. in which format)
     *
     * @return i18n translation key for the service name
     */
    @Override
    public String getServiceName() {
        return "ontopus.plugin.widoco.service.WidocoPublishingService.name";
    }

    /**
     * Sets data to the partially built ontology artifact in the context.
     *
     * @param formResult The result of the submitted form
     * @param context The context of importing process
     * @return Result with {@code null} value
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        WidocoArguments arguments = new WidocoArguments();
        formResult.formData().forEach((keyName, value) -> {
            WidocoArguments.Argument arg = WidocoArguments.Argument.valueOf(keyName);
            if (value.isString() || value.isNumber()) {
                arguments.put(arg, value.asString());
            } else if (value.isBoolean() || value.asBoolean()) {
                arguments.put(arg, "");
            }
        });

        return null;
    }
}
