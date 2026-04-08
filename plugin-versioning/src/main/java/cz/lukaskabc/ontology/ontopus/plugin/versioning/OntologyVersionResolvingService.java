package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.plugin.versioning.service.PredicateService;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

/** Resolves and uses version information and version IRI from the ontology */
@NullMarked
@Service
@Order(100)
public class OntologyVersionResolvingService implements OntologyVersioningService {
    private static final String TRANSLATION_ROOT =
            "ontopus.core.service.OntologyVersioningService.OntologyVersionResolvingService";
    private static final Set<URI> VERSION_EXAMPLES = Set.of(
            URI.create("http://purl.org/dc/terms/hasVersion"),
            URI.create("http://purl.org/pav/version"),
            URI.create("http://schema.org/schemaVersion"),
            URI.create("http://www.w3.org/ns/dcat#version"),
            URI.create("http://www.w3.org/2002/07/owl#versionInfo"));
    private static final Set<URI> VERSION_IRI_EXAMPLES = Set.of(URI.create("http://www.w3.org/2002/07/owl#versionIRI"));

    private static JsonForm makeForm(ObjectMapper objectMapper) {
        ObjectNode scheme = objectMapper.createObjectNode();
        ObjectNode uiScheme = objectMapper.createObjectNode();

        scheme.put("type", "object");
        scheme.put("$translationRoot", TRANSLATION_ROOT + ".form");

        ObjectNode properties = scheme.putObject("properties");

        ArrayNode versionExamples = objectMapper.createArrayNode();
        VERSION_EXAMPLES.stream().map(URI::toString).forEach(versionExamples::add);
        properties.putObject("version").put("type", "string").set("examples", versionExamples);

        ArrayNode versionIriExamples = objectMapper.createArrayNode();
        VERSION_IRI_EXAMPLES.stream().map(URI::toString).forEach(versionIriExamples::add);
        properties.putObject("versionIri").put("type", "string").set("examples", versionIriExamples);

        ObjectNode autocompleteOptions = objectMapper.createObjectNode();
        autocompleteOptions
                .put("ui:widget", "autocompleteWidget")
                .putObject("ui:options")
                .put("freeSolo", true)
                .put("openOnFocus", true)
                .put("disableClearable", true)
                .put("autoHighlight", true);

        uiScheme.set("version", autocompleteOptions).set("versionIri", autocompleteOptions);
        uiScheme.putObject("ui:globalOptions").put("enableMarkdownInDescription", true);

        return new JsonForm(scheme, uiScheme, null);
    }

    private final ObjectMapper objectMapper;
    private final PredicateService predicateService;

    private final JsonForm jsonForm;

    public OntologyVersionResolvingService(ObjectMapper objectMapper, PredicateService predicateService) {
        this.objectMapper = objectMapper;
        this.predicateService = predicateService;
        this.jsonForm = makeForm(objectMapper);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final JsonNode formData = Objects.requireNonNullElseGet(previousFormData, () -> newFormData(context));
        return jsonForm.withFormData(formData);
    }

    @Override
    public String getServiceName() {
        return TRANSLATION_ROOT + ".name";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        return null;
    }

    private ObjectNode newFormData(ReadOnlyImportProcessContext context) {
        final ResourceURI ontologyUri = context.getVersionSeries().getOntologyURI();
        final GraphURI contextUri = context.getTemporaryDatabaseContext();
        final String version = predicateService
                .findStatement(ontologyUri, VERSION_EXAMPLES, contextUri)
                .map(statement -> statement.getPredicate().stringValue())
                .orElse(null);
        final String versionIri = predicateService
                .findStatement(ontologyUri, VERSION_IRI_EXAMPLES, contextUri)
                .map(statement -> statement.getPredicate().stringValue())
                .orElse(null);

        ObjectNode formData = objectMapper.createObjectNode();
        formData.put("version", version);
        formData.put("versionIri", versionIri);
        return formData;
    }
}
