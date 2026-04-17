package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SingleFileSelectionService implements ImportProcessingService<Path> {
    private final JsonForm jsonForm;
    private final String translationRoot;
    private final ObjectMapper objectMapper;
    private final Predicate<Path> fileFilter;

    public SingleFileSelectionService(ObjectMapper objectMapper, String translationRoot, Predicate<Path> fileFilter) {
        this.objectMapper = objectMapper;
        this.translationRoot = translationRoot;
        this.fileFilter = fileFilter;
        this.jsonForm = makeJsonForm();
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final JsonNode schema = jsonForm.getJsonSchema();
        final JsonNode uiSchema = jsonForm.getUiSchema();
        final ObjectNode formData = Optional.ofNullable(previousFormData)
                .orElseGet(objectMapper::createObjectNode)
                .asObject();

        final ArrayNode examples =
                schema.get("properties").get("file").get("examples").asArray();

        try (Stream<Path> filePaths = FileUtils.listRecursively(context.getTempFolder())) {
            filePaths
                    .filter(this.fileFilter)
                    .map(context.getTempFolder()::relativize)
                    .map(Path::toString)
                    .forEach(examples::add);
        }

        if (!formData.hasNonNull("file") && !examples.isEmpty()) {
            formData.set("file", examples.get(0));
        }

        return new JsonForm(schema, uiSchema, formData);
    }

    @Override
    public String getServiceName() {
        return translationRoot + ".title";
    }

    @Override
    public Path handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        final String file = formResult.getStringValue("file");
        if (StringUtils.hasText(file)) {
            final Path relativePath = Path.of(file);
            final Path absolutePath = context.getTempFolder().resolve(relativePath);
            if (Files.exists(absolutePath)) {
                return absolutePath;
            }
            throw new JsonFormSubmitException("File does not exist: " + file);
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
