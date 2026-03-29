package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/** Import processing service that allows to select files from the specified directory using Glob pattern. */
public class FileSelectionService implements ImportProcessingService<List<Path>> {
    private static final String TRANSLATION_ROOT =
            "ontopus.core.service.OrderedImportPipelineService.FileImportSelectionService";
    static final Object CONTEXT_LAST_FORM_DATA_PROPERTY = new Object();

    protected static JsonForm makeJsonForm(ObjectMapper objectMapper) {
        final ObjectNode jsonSchema = objectMapper.createObjectNode();
        jsonSchema.put("$translationRoot", TRANSLATION_ROOT);
        final ObjectNode properties = jsonSchema.put("type", "object").putObject("properties");
        final ObjectNode pattern = properties.putObject("pattern");
        pattern.put("type", "string");

        properties.putObject("preview").put("type", "boolean");

        jsonSchema.putArray("required").add("pattern");

        return new JsonForm(jsonSchema, null, null);
    }

    private final ObjectMapper objectMapper;

    private final JsonForm jsonForm;

    private final String defaultGlobPattern;

    private final Path rootDirectory;

    public FileSelectionService(Path rootDirectory, ObjectMapper objectMapper, OntopusConfig config) {
        this.rootDirectory = rootDirectory;
        this.objectMapper = objectMapper;
        this.defaultGlobPattern = config.getFiles().getDefaultGlobPattern();
        this.jsonForm = makeJsonForm(objectMapper);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final JsonNode jsonSchema = this.jsonForm.getJsonSchema();
        final ObjectNode uiSchema = objectMapper.createObjectNode();
        final ObjectNode properties = (ObjectNode) jsonSchema.get("properties");
        final ObjectNode lastFormData = context.getAdditionalProperty(CONTEXT_LAST_FORM_DATA_PROPERTY, ObjectNode.class)
                .or(() -> Optional.ofNullable(previousFormData).map(JsonNode::asObject))
                .orElseGet(objectMapper::createObjectNode);
        Objects.requireNonNull(lastFormData);

        JsonNode patternNode = lastFormData.get("pattern");
        if (patternNode == null || !patternNode.isString()) {
            patternNode = objectMapper.stringNode(defaultGlobPattern);
            lastFormData.set("pattern", patternNode);
        }

        uiSchema.putObject("ui:globalOptions").put("enableMarkdownInDescription", true);

        final ObjectNode layout = uiSchema.put("ui:field", "LayoutGridField").putObject("ui:layoutGrid");
        final ObjectNode outerCol = layout.putObject("ui:col");
        final ArrayNode mainRow = outerCol.putArray("children");

        mainRow.add("pattern");

        final ObjectNode row = mainRow.addObject().putObject("ui:row");
        row.putObject("style").put("justify-content", "space-between");

        final ObjectNode columns = row.putArray("children").addObject().putObject("ui:columns");

        columns.put("className", "col-xs-6")
                .putArray("children")
                .add("files_list")
                .add("files_preview");

        properties
                .putObject("files_preview")
                .put("type", "null")
                .put("title", TRANSLATION_ROOT + "title")
                .put("description", listFilesAsString(patternNode.asString()));
        uiSchema.putObject("files_preview").put("ui:field", "typographyField").put("variant", "body1");

        properties
                .putObject("files_list")
                .put("type", "null")
                .put("title", TRANSLATION_ROOT + "title")
                .put("description", listFilesAsString("**"));
        uiSchema.putObject("files_list").put("ui:field", "typographyField").put("variant", "body1");

        mainRow.add("preview");

        return new JsonForm(jsonSchema, uiSchema, lastFormData);
    }

    @Override
    public String getServiceName() {
        return TRANSLATION_ROOT + ".name";
    }

    @Override
    public List<Path> handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        JsonNode formData = formResult.jsonFormData(objectMapper);
        context.setAdditionalProperty(CONTEXT_LAST_FORM_DATA_PROPERTY, formData);
        JsonNode doPreview = formData.get("preview");
        if (doPreview != null && doPreview.isBoolean() && doPreview.asBoolean()) {
            throw new JsonFormSubmitException("Preview files");
        }

        JsonNode pattern = formData.get("pattern");
        if (pattern == null || pattern.isNull() || pattern.isMissingNode()) {
            pattern = objectMapper.stringNode(defaultGlobPattern);
        }

        return listFiles(pattern.asString());
    }

    private boolean isPreviewEnabled(@Nullable JsonNode previousFormData) {
        if (previousFormData == null) {
            return false;
        }
        final JsonNode preview = previousFormData.get("preview");
        return preview != null && preview.isBoolean() && preview.asBoolean();
    }

    private List<Path> listFiles(String glob) {
        final PathMatcher globMatcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
        try (Stream<Path> stream = Files.walk(rootDirectory)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> {
                        final Path subpath = rootDirectory.relativize(path);
                        return globMatcher.matches(subpath);
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String listFilesAsString(String glob) {
        final List<String> files = listFiles(glob).stream()
                .map(rootDirectory::relativize)
                .map(Path::toString)
                .map(StringUtils::escapeMarkdown)
                .map("`%s`"::formatted)
                .toList();
        return files.isEmpty() ? "No files found" : " \\- " + String.join("\n \\- ", files);
    }
}
