package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyPublishingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.api.util.JsonUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.WidocoConstants;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.Argument;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.WidocoArgumentsFactory;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.WidocoControllerRegistrationService;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.WidocoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@Order(Ordered.LOWEST_PRECEDENCE)
@Service
public class WidocoPublishingService implements OntologyPublishingService, OrderedImportPipelineService<Void> {
    private static final String TRANSLATION_ROOT = "ontopus.plugin.widoco.service.WidocoPublishingService";

    private static final Logger log = LogManager.getLogger(WidocoPublishingService.class);

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
            if (WidocoConstants.WIDOCO_DISALLOWED_ARGS.contains(arg)) {
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

    private final ObjectMapper mapper;

    private final JsonForm jsonForm;
    private final WidocoControllerRegistrationService widocoControllerRegistrationService;
    private final WidocoArgumentsFactory argumentsFactory;
    private final WidocoService widocoService;

    public WidocoPublishingService(
            ContextToControllerMappingService mappingService,
            ObjectMapper mapper,
            WidocoControllerRegistrationService widocoControllerRegistrationService,
            ObjectMapper objectMapper,
            WidocoArgumentsFactory argumentsFactory,
            WidocoService widocoService) {
        this.mappingService = mappingService;
        this.mapper = mapper;
        this.widocoControllerRegistrationService = widocoControllerRegistrationService;
        this.jsonForm = makeForm(mapper);
        this.objectMapper = objectMapper;
        this.argumentsFactory = argumentsFactory;
        this.widocoService = widocoService;
    }

    private void createControllerMappings(ImportProcessContext context) {
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
            argumentsFactory
                    .resolveAlLanguages(context.getTemporaryDatabaseContext())
                    .forEach(langArray::add);
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
        WidocoArguments arguments = argumentsFactory.build(formResult, context);

        try {
            widocoService.runWidoco(arguments, context);
            createControllerMappings(context);
        } catch (OntopusException e) {
            throw e;
        } catch (Exception e) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_widoco)
                    .internalMessage("Failure during ontology publishing with Widoco")
                    .detailMessageArguments(new Object[] {e.getMessage()})
                    .detailMessageCode("ontopus.plugin.widoco.error.widocoExecution.detail")
                    .titleMessageCode("ontopus.plugin.widoco.error.widocoExecution.title")
                    .cause(e)
                    .build());
        }
        return null;
    }
}
