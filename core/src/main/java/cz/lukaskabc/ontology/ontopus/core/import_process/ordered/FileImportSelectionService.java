package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Service
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

    public FileImportSelectionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonForm = makeJsonForm(objectMapper);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final JsonNode jsonSchema = this.jsonForm.getJsonSchema();
        final ObjectNode uiSchema = objectMapper.createObjectNode();
        final ObjectNode properties = (ObjectNode) jsonSchema.get("properties");

        if (isPreviewEnabled(previousFormData)) {

        } else {
            properties
                    .putObject("files_list")
                    .put("type", "null")
                    .put("title", TRANSLATION_ROOT + "title")
                    .put("description", String.join("\n", listFiles(context)));
            uiSchema.putObject("files_list")
                    .put("ui:field", "typographyField")
                    .put("variant", "body1")
                    .put("ui:enableMarkdownInDescription", true);
        }
        return new JsonForm(jsonSchema, uiSchema, null);
    }

    @Override
    public String getServiceName() {
        return TRANSLATION_ROOT + ".name";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        return null;
    }

    private boolean isPreviewEnabled(@Nullable JsonNode previousFormData) {
        if (previousFormData == null) {
            return false;
        }
        final JsonNode preview = previousFormData.get("preview");
        return preview != null && preview.isBoolean() && preview.asBoolean();
    }

    private List<String> listFiles(ReadOnlyImportProcessContext context) {
        try (Stream<Path> stream = Files.walk(context.getTempFolder())) {
            return stream.filter(Files::isRegularFile)
                    .map(path -> path.subpath(context.getTempFolder().getNameCount(), path.getNameCount()))
                    .map(Path::toString)
                    .toList();
            // TODO: file structure of uploaded files is not preserved
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
