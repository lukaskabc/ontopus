package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyPublishingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.api.util.JsonUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.Argument;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.OntologyToFileSerializationService;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.WidocoControllerRegistrationService;
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
import tools.jackson.databind.node.StringNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Order(Ordered.LOWEST_PRECEDENCE)
@Service
public class WidocoPublishingService implements OntologyPublishingService, OrderedImportPipelineService<Void> {
    private static final String TRANSLATION_ROOT = "ontopus.plugin.widoco.service.WidocoPublishingService";
    private static final Set<String> WIDOCO_ONTOLOGY_EXTENSIONS_TO_REMOVE = Set.of("jsonld", "nt", "owl", "ttl");
    private static final Set<String> WIDOCO_OUTPUT_DIRECTORIES = Set.of("provenance", "sections", "resources");
    private static final Set<Argument> WIDOCO_DISALLOWED_ARGS = Set.of(Argument.ONT_FILE);

    private static void applyOrder(ObjectNode uiSchema, Collection<String> propertyNames) {
        ArrayNode order = uiSchema.putArray("ui:order").add("allLangs").add(Argument.LANG.name());

        propertyNames.forEach(propertyName -> {
            StringNode node = order.stringNode(propertyName);
            if (order.values().contains(node)) {
                return;
            }
            order.add(node);
        });
    }

    private static JsonForm makeForm(ObjectMapper mapper) {
        final ObjectNode schema = mapper.createObjectNode();
        final ObjectNode uiSchema = mapper.createObjectNode();
        final ObjectNode formData = mapper.createObjectNode();
        schema.put("type", "object");
        schema.put("$translationRoot", TRANSLATION_ROOT);
        final ObjectNode properties = schema.putObject("properties");

        for (Argument arg : Argument.values()) {
            if (WIDOCO_DISALLOWED_ARGS.contains(arg)) {
                continue;
            }
            final ObjectNode property = properties.putObject(arg.name()).put("type", arg.schemaType());
            if (arg.schemaType().startsWith("array")) {
                property.put("type", "array");
                property.putObject("items").put("type", arg.schemaType().substring("array_".length()));
            }
            if (arg.hasDefaultValue()) {
                formData.set(arg.name(), mapper.valueToTree(arg.defaultValue()));
            }
        }

        JsonUtils.getOrPutObject(properties, Argument.LANG.name()).put("uniqueItems", true);
        JsonUtils.getOrPutObject(uiSchema, Argument.LANG.name())
                .putObject("items")
                .put("ui:label", false);

        properties.putObject("allLangs").put("type", "boolean").put("default", true);

        ObjectNode ifWrapper = schema.putArray("allOf").addObject();
        JsonNode langNode = properties.remove(Argument.LANG.name());
        ifWrapper.putObject("if").putObject("properties").putObject("allLangs").put("const", false);
        ifWrapper.putObject("then").putObject("properties").set(Argument.LANG.name(), langNode);

        ObjectNode autocompleteWidget = mapper.createObjectNode();
        autocompleteWidget
                .put("ui:widget", "autocompleteWidget")
                .putObject("ui:options")
                .put("freeSolo", false)
                .put("openOnFocus", true)
                .put("disableClearable", true)
                .put("autoHighlight", true);

        uiSchema.set(Argument.IMPORT.name(), autocompleteWidget);

        uiSchema.putObject("ui:globalOptions").put("enableMarkdownInDescription", true);

        JsonUtils.getOrPutObject(uiSchema, Argument.LANG.name()).put("ui:orderable", false);

        applyOrder(uiSchema, properties.propertyNames());

        return new JsonForm(schema, uiSchema, formData);
    }

    private final ObjectMapper objectMapper;

    private final ContextToControllerMappingService mappingService;

    private final WidocoPluginConfig config;

    private final ObjectMapper mapper;

    private final JsonForm jsonForm;
    private final WidocoExecutionService widocoExecutionService;
    private final WidocoControllerRegistrationService widocoControllerRegistrationService;
    private final GraphService graphService;
    private final OntologyToFileSerializationService ontologyToFileSerializationService;

    public WidocoPublishingService(
            ContextToControllerMappingService mappingService,
            ObjectMapper mapper,
            WidocoExecutionService widocoExecutionService,
            WidocoPluginConfig config,
            WidocoControllerRegistrationService widocoControllerRegistrationService,
            GraphService graphService,
            OntologyToFileSerializationService ontologyToFileSerializationService,
            ObjectMapper objectMapper) {
        this.mappingService = mappingService;
        this.mapper = mapper;
        this.widocoExecutionService = widocoExecutionService;
        this.config = config;
        this.widocoControllerRegistrationService = widocoControllerRegistrationService;
        this.graphService = graphService;
        this.ontologyToFileSerializationService = ontologyToFileSerializationService;
        this.jsonForm = makeForm(mapper);
        this.objectMapper = objectMapper;
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
                .or(() -> Optional.ofNullable(jsonForm.getFormData()))
                .orElseGet(objectMapper::createObjectNode)
                .asObject();

        ArrayNode examples = mapper.createArrayNode();

        try (Stream<Path> filePaths = FileUtils.listRecursively(context.getTempFolder())) {
            filePaths
                    .map(context.getTempFolder()::relativize)
                    .map(Path::toString)
                    .forEach(examples::add);
        }

        if (previousFormData == null || previousFormData.get(Argument.LANG.name()) == null) {
            ArrayNode langArray = formData.putArray(Argument.LANG.name());
            resolveAlLanguages(context.getTemporaryDatabaseContext()).forEach(langArray::add);
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
        final boolean allLanguages = Optional.ofNullable(formResult.formData().remove("allLangs"))
                .filter(JsonNode::isBoolean)
                .map(JsonNode::asBoolean)
                .orElse(false);
        formResult.formData().forEach((keyName, value) -> {
            Argument arg = Argument.valueOf(keyName);
            if (WIDOCO_DISALLOWED_ARGS.contains(arg)) {
                return;
            }

            if (Argument.FILES.contains(arg) && value.isString()) {
                final Path relative = Path.of(value.asString());
                final Path inContext = FileUtils.resolvePath(context.getTempFolder(), relative);
                arguments.put(arg, inContext.toString());
            } else if (value.isString() || value.isNumber()) {
                arguments.put(arg, value.asString());
            } else if (value.isBoolean()) {
                if (value.asBoolean()) {
                    arguments.put(arg, "");
                }
            } else if (arg.equals(Argument.LANG) && value.isArray()) {
                Stream<String> languages;
                if (!allLanguages && !value.isEmpty() && value.isArray()) {
                    languages = value.asArray().values().stream()
                            .filter(JsonNode::isString)
                            .map(JsonNode::asString);
                } else {
                    languages = resolveAlLanguages(context.getTemporaryDatabaseContext());
                }

                final String param = languages.collect(Collectors.joining("-"));
                if (StringUtils.hasText(param)) {
                    arguments.put(arg, StringUtils.sanitize(param));
                }
            } else {
                throw new IllegalArgumentException("Unknown argument: " + arg + " with value " + value.toString());
            }
        });

        try {
            final Path workDir = context.createTempFolder(Path.of("widoco-work-dir-" + StringUtils.randomString(5)));
            final Path ontologyFile = workDir.resolve("ontology.ttl");
            ontologyToFileSerializationService.serializeOntologyToFile(
                    context.getVersionArtifact().getPrefixDeclarations(),
                    context.getTemporaryDatabaseContext(),
                    ontologyFile);

            arguments.put(Argument.ONT_FILE, ontologyFile.toString());

            final Path output = widocoExecutionService
                    .execute(arguments, workDir)
                    .get(config.getExecutionTimeout().toMillis() * 2, TimeUnit.MILLISECONDS);
            final Path outputRoot = resolveWidocoOutputRoot(output);
            persistOutput(outputRoot, context);
            // TODO: reduce size of this service
            final ContextToControllerMapping ontologyMapping = mappingService.createOntologyMapping(
                    context.getFinalDatabaseContext(),
                    widocoControllerRegistrationService.getControllerDescriptions(),
                    context.getControllerMappings());
            final ContextToControllerMapping resourceMapping = mappingService.createResourceMapping(
                    context.getFinalDatabaseContext(),
                    widocoControllerRegistrationService.getControllerDescriptions(),
                    context.getControllerMappings());
            context.addControllerMapping(ontologyMapping);
            context.addControllerMapping(resourceMapping);
        } catch (Exception e) {
            throw new OntopusException(e);
        }
        return null;
    }

    private void persistOutput(Path widocoOutput, ImportProcessContext context) {
        final String persistentContext = StringUtils.sanitizeUriAsComponent(
                context.getFinalDatabaseContext().toString());
        final Path filesDestination = FileUtils.resolvePath(config.getFilesDirectory(), Path.of(persistentContext));
        try {
            FileSystemUtils.deleteRecursively(filesDestination);
            FileSystemUtils.copyRecursively(widocoOutput, filesDestination);
        } catch (IOException e) {
            throw new OntopusException("Failed to persist widoco output", e);
        }
    }

    /**
     * Resolves all languages used by literals in the given graph. Languages containing "-" are excluded.
     *
     * @param graphURI the database graph
     * @return stream of language tags
     */
    private Stream<String> resolveAlLanguages(GraphURI graphURI) {
        final List<String> languages = graphService.findAllLanguageTags(graphURI);
        if (!languages.isEmpty()) {
            return languages.stream().filter(lang -> !lang.contains("-"));
        }
        return Stream.empty();
    }

    private Path resolveWidocoOutputRoot(Path output) {
        try (Stream<Path> filePaths = FileUtils.listRecursively(output)) {
            return filePaths
                    .filter(path -> WIDOCO_OUTPUT_DIRECTORIES.contains(
                            path.getFileName().toString()))
                    .findAny()
                    .map(Path::getParent)
                    .orElseThrow(() -> new OntopusException("Failed to find widoco output root folder"));
        }
    }
}
