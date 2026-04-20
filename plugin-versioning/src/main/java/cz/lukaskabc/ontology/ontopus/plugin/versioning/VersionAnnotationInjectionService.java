package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyAnnotationInjectionService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.*;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionArtifactService;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Injects Version and Version IRI into the ontology */
@Service
public class VersionAnnotationInjectionService implements OntologyAnnotationInjectionService {
    private final ObjectMapper objectMapper;
    private final PredicateService predicateService;
    private final GraphService graphService;
    private final VersionArtifactService versionArtifactService;

    public VersionAnnotationInjectionService(
        ObjectMapper objectMapper, PredicateService predicateService, GraphService graphService, VersionArtifactService versionArtifactService) {
        this.objectMapper = objectMapper;
        this.predicateService = predicateService;
        this.graphService = graphService;
        this.versionArtifactService = versionArtifactService;
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

        final String previousVersionValue = findPreviousVersion(context);
        final Statement previousVersion = getPreviousVersionPredicate(context, previousFormData)
            .map(predicate -> findStatement(context, predicate))
            .orElse(null);
        final boolean previousVersionValueMatches = previousVersionValueMatches(previousVersionValue, previousVersion);

        if (versionValueMatches && versionIriValueMatches && previousVersionValueMatches) {
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
        if (previousVersion != null && formData.has("previousVersionPredicate")) {
            formData.put("previousVersionPredicate", previousVersion.getObject().stringValue());
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

        if (!previousVersionValueMatches) {
            ArrayNode previousVersionExamples = objectMapper.createArrayNode();
            VersioningPlugin.PREVIOUS_VERSION_EXAMPLE.stream()
                .map(URI::toString).forEach(previousVersionExamples::add);
            properties.putObject("previousVersionPredicate").put("type", "string").set("examples", previousVersionExamples);
        }

        ObjectNode autocompleteOptions = objectMapper.createObjectNode();
        autocompleteOptions
                .put("ui:widget", "autocompleteWidget")
                .putObject("ui:options")
                .put("freeSolo", true)
                .put("openOnFocus", true)
                .put("disableClearable", true)
                .put("autoHighlight", true);

        uiSchema.set("versionPredicate", autocompleteOptions).set("versionIriPredicate", autocompleteOptions).set("previousVersionPredicate", autocompleteOptions);
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

    private Optional<ResourceURI> getPreviousVersionPredicate(ReadOnlyImportProcessContext context, @Nullable JsonNode formdata) {
        return context.getAdditionalProperty(VersioningContextParameters.PREVIOUS_VERSION_IRI_PREDICATE, ResourceURI.class)
            .or(() -> Optional.ofNullable(formdata)
                .map(data -> data.get("previousVersionPredicate"))
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

        final String previousVersionValue = findPreviousVersion(context);
        final Statement previousVersion = getPreviousVersionPredicate(context, formData)
            .map(predicate -> findStatement(context, predicate))
            .orElse(null);
        final boolean previousVersionValueMatches = previousVersionValueMatches(previousVersionValue, previousVersion);

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



        if (!previousVersionValueMatches && previousVersionValue != null) {
            if (previousVersion != null) {
                statementsToDelete.add(previousVersion);
            }

            final IRI previousPredicate = vf.createIRI(formResult.getStringValue("previousVersionPredicate"));
            final IRI previousVersionIRI =
                vf.createIRI(previousVersionValue);
            final Statement previousVersionStatement =
                SimpleValueFactory.getInstance().createStatement(ontologyIRI, previousPredicate, previousVersionIRI, contextIRI);
            statements.add(previousVersionStatement);

        }

        graphService.delete(statementsToDelete, context.getTemporaryDatabaseContext());
        return new LinkedHashModel(statements);
    }

    @Nullable
    private String findPreviousVersion(ReadOnlyImportProcessContext context) {
        final VersionArtifactURI latest = context.getVersionSeries().getLast();
        if (latest == null) {
            return null;
        }
        return versionArtifactService.findById(latest).map(VersionArtifact::getVersionUri).map(OntologyVersionURI::toString).orElse(null);
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

    private boolean previousVersionValueMatches(@Nullable String previousVersion, @Nullable Statement statement) {
        return statement != null &&
            previousVersion != null
            && statement.getObject()
            .stringValue()
            .equals(previousVersion);
    }
}
