package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@SessionScope
@Order(ImportProcessServiceOrder.DATA_LOADING_SELECTION_SERVICE + 1)
public class FileImportSelectionService implements OrderedImportPipelineService<Void> {
    private static final String TRANSLATION_ROOT =
            "ontopus.core.service.OrderedImportPipelineService.FileImportSelectionService";

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

    @Nullable private ObjectNode lastFormData = null;

    public FileImportSelectionService(ObjectMapper objectMapper, OntopusConfig config) {
        this.objectMapper = objectMapper;
        this.defaultGlobPattern = config.getFiles().getDefaultGlobPattern();
        this.jsonForm = makeJsonForm(objectMapper);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final JsonNode jsonSchema = this.jsonForm.getJsonSchema();
        final ObjectNode uiSchema = objectMapper.createObjectNode();
        final ObjectNode properties = (ObjectNode) jsonSchema.get("properties");
        lastFormData = Optional.ofNullable(lastFormData)
                .or(() -> Optional.ofNullable(previousFormData).map(JsonNode::asObject))
                .orElseGet(objectMapper::createObjectNode);

        uiSchema.putObject("pattern").put("ui:enableMarkdownInDescription", true);

        JsonNode patternNode = lastFormData.get("pattern");
        if (patternNode == null || !patternNode.isString()) {
            patternNode = lastFormData.put("pattern", defaultGlobPattern);
        }

        if (isPreviewEnabled(lastFormData)) {
            properties
                    .putObject("files_preview")
                    .put("type", "null")
                    .put("title", TRANSLATION_ROOT + "title")
                    .put("description", listFiles(context, patternNode.asString()));
            uiSchema.putObject("files_preview")
                    .put("ui:field", "typographyField")
                    .put("variant", "body1")
                    .put("ui:enableMarkdownInDescription", true);
        }
        properties
                .putObject("files_list")
                .put("type", "null")
                .put("title", TRANSLATION_ROOT + "title")
                .put("description", listFiles(context, "**"));
        uiSchema.putObject("files_list").put("ui:field", "typographyField").put("variant", "body1");

        return new JsonForm(jsonSchema, uiSchema, lastFormData);
    }

    @Override
    public String getServiceName() {
        return TRANSLATION_ROOT + ".name";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        this.lastFormData = formResult.jsonFormData(objectMapper);
        JsonNode doPreview = lastFormData.get("preview");
        if (doPreview != null && doPreview.isBoolean() && doPreview.asBoolean()) {
            throw new JsonFormSubmitException("Preview files");
        }

        // TODO finish implementation and import selected files
        return null;
    }

    private boolean isPreviewEnabled(@Nullable JsonNode previousFormData) {
        if (previousFormData == null) {
            return false;
        }
        final JsonNode preview = previousFormData.get("preview");
        return preview != null && preview.isBoolean() && preview.asBoolean();
    }

    private String listFiles(ReadOnlyImportProcessContext context, String glob) {
        final PathMatcher globMatcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
        try (Stream<Path> stream = Files.walk(context.getTempFolder())) {
            final List<String> files = stream.filter(Files::isRegularFile)
                    .map(path -> path.subpath(context.getTempFolder().getNameCount(), path.getNameCount()))
                    .filter(globMatcher::matches)
                    .map(Path::toString)
                    .toList();
            return files.isEmpty() ? "No files found" : " - " + String.join("\n - ", files);
            // TODO: file structure of uploaded files is not preserved
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
