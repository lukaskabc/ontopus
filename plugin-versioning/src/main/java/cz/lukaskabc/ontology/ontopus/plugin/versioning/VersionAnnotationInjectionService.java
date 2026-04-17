package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyAnnotationInjectionService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import cz.lukaskabc.ontology.ontopus.plugin.versioning.service.PredicateService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/** Injects Version and Version IRI into the ontology */
@Service
public class VersionAnnotationInjectionService implements OntologyAnnotationInjectionService {
    private final ObjectMapper objectMapper;
    private final PredicateService predicateService;
    private final GraphService graphService;

    public VersionAnnotationInjectionService(
            ObjectMapper objectMapper, PredicateService predicateService, GraphService graphService) {
        this.objectMapper = objectMapper;
        this.predicateService = predicateService;
        this.graphService = graphService;
    }

    @Nullable private Statement findStatement(ReadOnlyImportProcessContext context, @Nullable ResourceURI predicate) {
        if (predicate == null) {
            return null;
        }
        final OntologyURI ontologyURI = context.getVersionSeries().getOntologyURI();
        final GraphURI contextURI = context.getTemporaryDatabaseContext();
        return predicateService
                .findStatement(ontologyURI, Set.of(predicate.toURI()), contextURI)
                .orElse(null);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        final Statement version = getVersionPredicate(context, previousFormData)
                .map(predicate -> findStatement(context, predicate))
                .orElse(null);
        final boolean versionValueMatches = versionValueMatches(context, version);

        final Statement versionIri = getVersionIriPredicate(context, previousFormData)
                .map(predicate -> findStatement(context, predicate))
                .orElse(null);
        final boolean versionIriValueMatches = versionIriValueMatches(context, versionIri);

        if (versionValueMatches && versionIriValueMatches) {
            return null;
        }

        ObjectNode formData = Optional.ofNullable(previousFormData)
                .orElseGet(objectMapper::createObjectNode)
                .asObject();

        if (version != null && !formData.has("versionPredicate")) {
            formData.put("versionPredicate", version.getObject().stringValue());
        }
        if (versionIri != null && !formData.has("versionIriPredicate")) {
            formData.put("versionIriPredicate", versionIri.getObject().stringValue());
        }

        ObjectNode schema = objectMapper.createObjectNode().put("type", "object");
        ObjectNode uiSchema = objectMapper.createObjectNode();

        schema.put(
                "$translationRoot",
                "ontopus.core.service.ImportProcessingService.OntologyAnnotationInjectionService.VersionAnnotationInjectionService");

        ObjectNode properties = schema.putObject("properties");

        if (!versionValueMatches) {
            ArrayNode versionExamples = objectMapper.createArrayNode();
            VersioningPlugin.VERSION_EXAMPLES.stream().map(URI::toString).forEach(versionExamples::add);

            properties.putObject("versionPredicate").put("type", "string").set("examples", versionExamples);
        }

        if (!versionIriValueMatches) {
            ArrayNode versionIriExamples = objectMapper.createArrayNode();
            VersioningPlugin.VERSION_IRI_EXAMPLES.stream().map(URI::toString).forEach(versionIriExamples::add);
            properties.putObject("versionIriPredicate").put("type", "string").set("examples", versionIriExamples);
        }

        ObjectNode autocompleteOptions = objectMapper.createObjectNode();
        autocompleteOptions
                .put("ui:widget", "autocompleteWidget")
                .putObject("ui:options")
                .put("freeSolo", true)
                .put("openOnFocus", true)
                .put("disableClearable", true)
                .put("autoHighlight", true);

        uiSchema.set("versionPredicate", autocompleteOptions).set("versionIriPredicate", autocompleteOptions);
        uiSchema.putObject("ui:globalOptions").put("enableMarkdownInDescription", true);

        return new JsonForm(schema, uiSchema, formData);
    }

    /**
     * Provides the description about what is being injected into the ontology.
     *
     * @return i18n translation key for the service description
     */
    @Override
    public String getServiceDescription() {
        return "ontopus.core.service.ImportProcessingService.OntologyAnnotationInjectionService";
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    private Optional<ResourceURI> getVersionIriPredicate(
            ReadOnlyImportProcessContext context, @Nullable JsonNode formData) {
        return context.getAdditionalProperty(VersioningContextParameters.VERSION_IRI_PREDICATE, ResourceURI.class)
                .or(() -> Optional.ofNullable(formData)
                        .map(data -> data.get("versionIriPredicate"))
                        .filter(JsonNode::isString)
                        .map(JsonNode::stringValue)
                        .map(ResourceURI::new));
    }

    private Optional<ResourceURI> getVersionPredicate(
            ReadOnlyImportProcessContext context, @Nullable JsonNode formData) {
        return context.getAdditionalProperty(VersioningContextParameters.VERSION_PREDICATE, ResourceURI.class)
                .or(() -> Optional.ofNullable(formData)
                        .map(data -> data.get("versionPredicate"))
                        .filter(JsonNode::isString)
                        .map(JsonNode::stringValue)
                        .map(ResourceURI::new));
    }

    /**
     * Accepts and handles the result of submitted form. If the service does not provide a form, it returns {@code null}
     * from {@link #getJsonForm(ReadOnlyImportProcessContext, JsonNode)} then this method will be called with an empty
     * form result without users interaction.
     *
     * @param formResult The data submitted in the form
     * @param context The import process context
     * @return The result of the operation
     * @throws JsonFormSubmitException when the submitted form data is invalid. When the exception is thrown, the form
     *     result won't be saved in the context and the service will remain on the stack.
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    @Override
    public Model handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        final ObjectNode formData = formResult.jsonFormData(objectMapper);
        final Statement version = getVersionPredicate(context, formData)
                .map(predicate -> findStatement(context, predicate))
                .orElse(null);
        final boolean versionValueMatches = versionValueMatches(context, version);

        final Statement versionIri = getVersionIriPredicate(context, formData)
                .map(predicate -> findStatement(context, predicate))
                .orElse(null);
        final boolean versionIriValueMatches = versionIriValueMatches(context, versionIri);

        Set<Statement> statements = new HashSet<>(2);
        Set<Statement> statementsToDelete = new HashSet<>(2);
        final SimpleValueFactory vf = SimpleValueFactory.getInstance();
        final IRI ontologyIRI =
                vf.createIRI(context.getVersionSeries().getOntologyURI().toString());
        final IRI contextIRI =
                vf.createIRI(context.getTemporaryDatabaseContext().toString());

        if (!versionValueMatches) {
            if (version != null) {
                statementsToDelete.add(version);
            }
            final IRI versionPredicate = vf.createIRI(formResult.getStringValue("versionPredicate"));
            final Literal versionValue =
                    vf.createLiteral(context.getVersionArtifact().getVersion());
            final Statement versionStatement = SimpleValueFactory.getInstance()
                    .createStatement(ontologyIRI, versionPredicate, versionValue, contextIRI);
            statements.add(versionStatement);
        }

        if (!versionIriValueMatches) {
            if (versionIri != null) {
                statementsToDelete.add(versionIri);
            }
            final IRI iriPredicate = vf.createIRI(formResult.getStringValue("versionIriPredicate"));
            final IRI versionIRI =
                    vf.createIRI(context.getVersionArtifact().getVersionUri().toString());
            final Statement versionIriStatement =
                    SimpleValueFactory.getInstance().createStatement(ontologyIRI, iriPredicate, versionIRI, contextIRI);
            statements.add(versionIriStatement);
        }

        graphService.delete(statementsToDelete, context.getTemporaryDatabaseContext());
        return new LinkedHashModel(statements);
    }

    private boolean versionIriValueMatches(ReadOnlyImportProcessContext context, @Nullable Statement statement) {
        return statement != null
                && statement
                        .getObject()
                        .stringValue()
                        .equals(context.getVersionArtifact().getVersionUri().toString());
    }

    private boolean versionValueMatches(ReadOnlyImportProcessContext context, @Nullable Statement statement) {
        return statement != null
                && statement
                        .getObject()
                        .stringValue()
                        .equals(context.getVersionArtifact().getVersion());
    }
}
