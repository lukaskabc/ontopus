package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.nio.file.Path;

public class SingleFileSelectionService implements ImportProcessingService<Path> {
    private final JsonForm jsonForm;
    private final String translationRoot;
    private final ObjectMapper objectMapper;

    public SingleFileSelectionService(ObjectMapper objectMapper, String translationRoot) {
        this.objectMapper = objectMapper;
        this.translationRoot = translationRoot;
        this.jsonForm = makeJsonForm();
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final JsonNode schema = jsonForm.getJsonSchema();
        final JsonNode uiSchema = jsonForm.getUiSchema();

        final ArrayNode examples =
                schema.get("properties").get("file").get("examples").asArray();

        FileUtils.listRecursively(context.getTempFolder())
                .map(context.getTempFolder()::relativize)
                .map(Path::toString)
                .forEach(examples::add);

        return new JsonForm(schema, uiSchema, previousFormData);
    }

    @Override
    public String getServiceName() {
        return translationRoot + ".name";
    }

    @Override
    public Path handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        final String file = formResult.getStringValue("file");
        if (StringUtils.hasText(file)) {
            final Path relativePath = Path.of(file);
            return context.getTempFolder().resolve(relativePath);
        }
        throw new JsonFormSubmitException("File path cannot be empty");
    }

    private JsonForm makeJsonForm() {
        ObjectNode scheme = objectMapper.createObjectNode();
        scheme.put("$translationRoot", translationRoot);
        scheme.put("type", "object");

        ObjectNode properties = scheme.putObject("properties");
        properties.putObject("file").put("type", "string").putArray("examples");

        ObjectNode uiSchema = objectMapper.createObjectNode();
        uiSchema.putObject("file")
                .put("ui:widget", "autocompleteWidget")
                .putObject("ui:options")
                .put("freeSolo", false)
                .put("openOnFocus", true)
                .put("disableClearable", true)
                .put("autoHighlight", true);

        return new JsonForm(scheme, uiSchema, null);
    }
}
