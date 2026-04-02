package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyPublishingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.Argument;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.WidocoExecutionService;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Order(Ordered.LOWEST_PRECEDENCE)
@Service
public class WidocoPublishingService implements OntologyPublishingService, OrderedImportPipelineService<Void> {
    private static final String TRANSLATION_ROOT = "ontopus.plugin.widoco.service.WidocoPublishingService";
    private static final Set<String> WIDOCO_ONTOLOGY_EXTENSIONS_TO_REMOVE = Set.of("jsonld", "nt", "owl", "ttl");
    private static final String REDIRECT_SERIALIZATION_FIELD = "RedirectSerializationToOntopus";

    private static JsonForm makeForm(ObjectMapper mapper) {
        final ObjectNode schema = mapper.createObjectNode();
        final ObjectNode uiSchema = mapper.createObjectNode();
        schema.put("type", "object");
        schema.put("$translationRoot", TRANSLATION_ROOT);
        final ObjectNode properties = schema.putObject("properties");

        for (Argument arg : Argument.values()) {
            properties.putObject(arg.name()).put("type", arg.schemaType());
        }

        properties.putObject(REDIRECT_SERIALIZATION_FIELD).put("type", "boolean");

        ObjectNode autocompleteWidget = mapper.createObjectNode();
        autocompleteWidget
                .put("ui:widget", "autocompleteWidget")
                .putObject("ui:options")
                .put("freeSolo", false)
                .put("openOnFocus", true)
                .put("disableClearable", true)
                .put("autoHighlight", true);

        uiSchema.set(Argument.CONF_FILE.name(), autocompleteWidget);
        uiSchema.set(Argument.ONT_FILE.name(), autocompleteWidget);

        return new JsonForm(schema, uiSchema, null);
    }

    private final WidocoPluginConfig config;

    private final ObjectMapper mapper;

    private final JsonForm jsonForm;
    private final WidocoExecutionService widocoExecutionService;

    public WidocoPublishingService(
            ObjectMapper mapper, WidocoExecutionService widocoExecutionService, WidocoPluginConfig config) {
        this.mapper = mapper;
        this.widocoExecutionService = widocoExecutionService;
        this.config = config;
        this.jsonForm = makeForm(mapper);
    }

    private void deleteUnusedWidocoFiles(Path widocoRoot, boolean deleteSerializations) throws IOException {
        Files.deleteIfExists(widocoRoot.resolve("readme.md"));
        if (deleteSerializations) {
            for (String ext : WIDOCO_ONTOLOGY_EXTENSIONS_TO_REMOVE) {
                Path path = widocoRoot.resolve("ontology." + ext);
                Files.deleteIfExists(path);
            }
        }
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
        final ObjectNode formData = Optional.ofNullable(previousFormData)
                .orElseGet(mapper::createObjectNode)
                .asObject();

        ArrayNode examples = mapper.createArrayNode();

        FileUtils.listRecursively(context.getTempFolder())
                .map(context.getTempFolder()::relativize)
                .map(Path::toString)
                .forEach(examples::add);

        final JsonNode properties = schema.get("properties");
        properties.get(Argument.CONF_FILE.name()).asObject().set("examples", examples);
        properties.get(Argument.ONT_FILE.name()).asObject().set("examples", examples);

        if (context.getOntologyFilePath() != null) {
            final String ontologyFile = context.getTempFolder()
                    .relativize(context.getOntologyFilePath())
                    .toString();
            formData.put(Argument.ONT_FILE.name(), ontologyFile);
        }

        return new JsonForm(schema, uiSchema, formData);
    }

    /**
     * Provides information about actions of this service. How the ontology will be published (e.g. in which format)
     *
     * @return i18n translation key for the service name
     */
    @Override
    public String getServiceName() {
        return TRANSLATION_ROOT + ".name";
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
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        WidocoArguments arguments = new WidocoArguments();
        formResult.formData().forEach((keyName, value) -> {
            if (keyName.equals(REDIRECT_SERIALIZATION_FIELD)) {
                return;
            }
            Argument arg = Argument.valueOf(keyName);

            if (Argument.FILES.contains(arg) && value.isString()) {
                final Path relative = Path.of(value.asString());
                final Path inContext = FileUtils.resolvePath(context.getTempFolder(), relative);
                arguments.put(arg, inContext.toString());
            } else if (value.isString() || value.isNumber()) {
                arguments.put(arg, value.asString());
            } else if (value.isBoolean() || value.asBoolean()) {
                arguments.put(arg, "");
            }
        });

        boolean redirectSerialization = formResult
                .formData()
                .getOrDefault(
                        REDIRECT_SERIALIZATION_FIELD, mapper.getNodeFactory().booleanNode(false))
                .asBoolean();

        try {
            final Path output = widocoExecutionService
                    .execute(arguments)
                    .get(config.getExecutionTimeout().toMillis() * 2, TimeUnit.MILLISECONDS);
            final Path outputRoot = resolveWidocoOutputRoot(output);
            deleteUnusedWidocoFiles(outputRoot, redirectSerialization);
            persistOutput(outputRoot, context);
        } catch (Exception e) {
            throw new OntopusException(e);
        }
        return null;
    }

    private void persistOutput(Path widocoOutput, ImportProcessContext context) {
        // TODO: persist output to persistent context
        final String persistentContext =
                StringUtils.sanitize(context.getFinalDatabaseContext().toString());
        final Path filesDestination = FileUtils.resolvePath(config.getFilesDirectory(), Path.of(persistentContext));
        try {
            FileSystemUtils.deleteRecursively(filesDestination);
            // TODO move?
            FileSystemUtils.copyRecursively(widocoOutput, filesDestination);
        } catch (IOException e) {
            throw new OntopusException("Failed to persist widoco output", e);
        }
    }

    private Path resolveWidocoOutputRoot(Path output) {
        return FileUtils.listRecursively(output)
                .filter(path -> path.getFileName().toString().equals("sections"))
                .findAny()
                .map(Path::getParent)
                .orElseThrow(() -> new OntopusException("Failed to find widoco output root folder"));
    }
}
