package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.versioning.service.PredicateService;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/** Resolves and uses version information and version IRI from the ontology */
@NullMarked
@Service
@Order(100)
public class OntologyVersionResolvingService implements OntologyVersioningService {
    private static final String TRANSLATION_ROOT =
            "ontopus.core.service.OntologyVersioningService.OntologyVersionResolvingService";

    private static JsonForm makeForm(ObjectMapper objectMapper) {
        ObjectNode scheme = objectMapper.createObjectNode();
        ObjectNode uiScheme = objectMapper.createObjectNode();

        scheme.put("type", "object");
        scheme.put("$translationRoot", TRANSLATION_ROOT + ".form");

        ObjectNode properties = scheme.putObject("properties");

        ArrayNode versionExamples = objectMapper.createArrayNode();
        VersioningPlugin.VERSION_EXAMPLES.stream().map(URI::toString).forEach(versionExamples::add);
        properties.putObject("version").put("type", "string").set("examples", versionExamples);

        ArrayNode versionIriExamples = objectMapper.createArrayNode();
        VersioningPlugin.VERSION_IRI_EXAMPLES.stream().map(URI::toString).forEach(versionIriExamples::add);
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

    private static JsonFormSubmitException missingValueException(String paramName) {
        return JsonFormSubmitException.builder()
                .errorType(Vocabulary.u_i_form_submit)
                .internalMessage("Form data are missing value for " + paramName)
                .titleMessageCode("ontopus.core.error.form.missingValue.title")
                .detailMessageArguments(new Object[] {paramName})
                .detailMessageCode("ontopus.core.error.form.missingValue.detail")
                .build();
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

    private String getTripleValue(URI predicate, ImportProcessContext context, String paramName) {
        final OntologyURI ontologyURI = context.getVersionSeries().getOntologyURI();
        return predicateService
                .findStatement(ontologyURI, List.of(predicate), context.getTemporaryDatabaseContext())
                .map(Statement::getObject)
                .map(Value::stringValue)
                .orElseThrow(() -> JsonFormSubmitException.builder()
                        .errorType(Vocabulary.u_i_no_predicate)
                        .internalMessage(
                                "Predicate <" + predicate + "> does not exist on the ontology <" + ontologyURI + ">")
                        .titleMessageCode("ontopus.plugin.versioning.error.noPredicate.title")
                        .detailMessageArguments(new Object[] {paramName, predicate})
                        .detailMessageCode("ontopus.plugin.versioning.error.noPredicate.detail")
                        .build());
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        final String version = formResult.getStringValue("version");
        final String versionIri = formResult.getStringValue("versionIri");
        if (!StringUtils.hasText(version)) {
            throw missingValueException("version");
        }
        if (!StringUtils.hasText(versionIri)) {
            throw missingValueException("version IRI");
        }

        final URI versionPredicate = URI.create(version);
        final URI versionIriPredicate = URI.create(versionIri);

        final String versionValue = getTripleValue(versionPredicate, context, "version");
        final OntologyVersionURI versionIriValue =
                new OntologyVersionURI(getTripleValue(versionIriPredicate, context, "version IRI"));

        context.getVersionArtifact().setVersion(versionValue);
        context.getVersionArtifact().setVersionUri(versionIriValue);

        context.setAdditionalProperty(VersioningContextParameters.VERSION_PREDICATE, new ResourceURI(version));
        context.setAdditionalProperty(
                VersioningContextParameters.VERSION_IRI_PREDICATE, new ResourceURI(versionIriPredicate));

        return null;
    }

    private ObjectNode newFormData(ReadOnlyImportProcessContext context) {
        final ResourceURI ontologyUri = context.getVersionSeries().getOntologyURI();
        final GraphURI contextUri = context.getTemporaryDatabaseContext();
        final String version = predicateService
                .findStatement(ontologyUri, VersioningPlugin.VERSION_EXAMPLES, contextUri)
                .map(statement -> statement.getPredicate().stringValue())
                .orElse(null);
        final String versionIri = predicateService
                .findStatement(ontologyUri, VersioningPlugin.VERSION_IRI_EXAMPLES, contextUri)
                .map(statement -> statement.getPredicate().stringValue())
                .orElse(null);

        ObjectNode formData = objectMapper.createObjectNode();
        formData.put("version", version);
        formData.put("versionIri", versionIri);
        return formData;
    }
}
