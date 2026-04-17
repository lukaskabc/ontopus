package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OntologyIdentifierSelector implements ImportProcessingService<URI> {
    private static final String ID_FIELD = "identifier";
    private final JsonForm jsonForm;
    private final ObjectMapper objectMapper;
    private final GraphService graphService;
    private final String translationRoot;

    public OntologyIdentifierSelector(
            ObjectMapper objectMapper, GraphService graphService, Set<URI> identifiers, String translationRoot) {
        this.objectMapper = objectMapper;
        this.graphService = graphService;
        this.translationRoot = translationRoot;
        this.jsonForm = makeJsonForm(identifiers);
    }

    private void ensureExists(ResourceURI resource, ImportProcessContext context) throws JsonFormSubmitException {
        if (!graphService.subjectExists(resource, context.getTemporaryDatabaseContext())) {
            throw JsonFormSubmitException.builder()
                    .errorType(Vocabulary.u_i_form_submit)
                    .internalMessage("Resource does not exists in the ontology")
                    .titleMessageCode("ontopus.core.error.notFound.title")
                    .detailMessageArguments(new Object[] {resource})
                    .detailMessageCode("ontopus.core.error.notFound.resource")
                    .build();
        }
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        if (previousFormData != null) {
            return jsonForm.withFormData(previousFormData);
        }
        return jsonForm;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public URI handleSubmit(FormResult formResult, ImportProcessContext context) {
        String identifier = formResult.getStringValue(ID_FIELD);
        if (identifier == null) {
            throw JsonFormSubmitException.missingValue("ontology identifier");
        }
        final URI uri = URI.create(identifier);
        ensureExists(new ResourceURI(uri), context);
        return uri;
    }

    protected JsonForm makeJsonForm(Set<URI> identifiers) {
        List<String> stringIds =
                identifiers.stream().map(URI::toString).sorted().toList();
        Set<StringNode> examples = stringIds.stream().map(StringNode::valueOf).collect(Collectors.toSet());

        ObjectNode scheme = objectMapper.createObjectNode();
        scheme.put("type", "object").put("$translationRoot", translationRoot);
        ObjectNode properties = scheme.putObject("properties");
        properties
                .putObject(ID_FIELD)
                .put("type", "string")
                // not limiting the format to URI
                // since the frontend URI validation may be too strict
                .put(
                        "title",
                        "ontopus.core.service.ImportProcessingService.OntologyIdentifierSelector.field.identifier.title")
                .putArray("examples")
                .addAll(examples);

        ObjectNode formData = objectMapper.createObjectNode();
        if (!stringIds.isEmpty()) {
            formData.put(ID_FIELD, stringIds.getFirst());
        }

        ObjectNode uiSchema = objectMapper.createObjectNode();
        uiSchema.putObject(ID_FIELD)
                .put("ui:widget", "autocompleteWidget")
                .putObject("ui:options")
                .put("freeSolo", true)
                .put("openOnFocus", true)
                .put("disableClearable", true)
                .put("autoHighlight", true);

        return new JsonForm(scheme, uiSchema, formData);
    }
}
