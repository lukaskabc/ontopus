package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyPublishingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.JsonFormUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoArguments;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
public class WidocoPublishingService implements OntologyPublishingService {
    private static JsonForm makeForm(ObjectMapper mapper) {
        final ObjectNode schema = mapper.createObjectNode();
        for (WidocoArguments.Argument arg : WidocoArguments.Argument.values()) {
            final WidocoArguments.Key<?> key = WidocoArguments.Key.from(arg);
            schema.putObject(arg.name()).put("type", JsonFormUtils.schemaTypeForClass(key.type()));
        }

        return new JsonForm(schema, null, null);
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
        return jsonForm;
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

        return null;
    }
}
