package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Dataset;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Dataset_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Objects;

@Service
@Order(ImportProcessServiceOrder.ARTIFACT_REVIEW_SERVICE)
public class ArtifactReviewService implements OrderedImportPipelineService<Void> {

    private static void applyArtifactProperties(ObjectNode properties, ObjectNode uiSchema) {
        properties
                .putObject(VersionArtifact_.versionUri.getName())
                .put("type", "string")
                .put("title", "entity.version-artifact.detail.versionUri");
        uiSchema.putObject(VersionArtifact_.versionUri.getName());

        properties
                .putObject(VersionArtifact_.version.getName())
                .put("type", "string")
                .put("title", "entity.version-artifact.detail.version");

        uiSchema.putObject(VersionArtifact_.version.getName());
    }

    private static void applyDatasetProperties(ObjectNode properties, ObjectNode uiSchema) {
        ObjectNode additionalStringProperties =
                uiSchema.objectNode().put("type", "string").put("minLength", 1);

        properties
                .putObject(Dataset_.title.getName())
                .put("type", "object")
                .put("title", "entity.dataset.detail.title")
                .put("minProperties", 1)
                .set("additionalProperties", additionalStringProperties);
        uiSchema.putObject(Dataset_.title.getName())
                .put("ui:field", "multilingualStringField")
                .put("ui:readonly", false);

        properties
                .putObject(Dataset_.description.getName())
                .put("type", "object")
                .put("title", "entity.dataset.detail.description")
                .set("additionalProperties", additionalStringProperties);
        uiSchema.putObject(Dataset_.description.getName())
                .put("ui:field", "multilingualStringField")
                .put("multiline", true)
                .put("ui:readonly", false);
    }

    private static void applyVersionSeriesProperties(ObjectNode properties, ObjectNode uiSchema) {
        properties
                .putObject(VersionSeries_.ontologyURI.getName())
                .put("type", "string")
                .put("title", "entity.version-series.detail.ontologyIdentifier");
        uiSchema.putObject(VersionSeries_.ontologyURI.getName());
    }

    private static JsonForm makeJsonForm(ObjectMapper objectMapper) {
        ObjectNode scheme = objectMapper.createObjectNode();
        ObjectNode uiSchema = objectMapper.createObjectNode();

        scheme.put("type", "object").put("$translationRoot", "entity");
        uiSchema.putObject("ui:globalOptions").put("readonly", true).put("enableMarkdownInDescription", true);

        ObjectNode properties = scheme.putObject("properties");

        ObjectNode seriesProperties = properties
                .putObject("version-series")
                .put("type", "object")
                .put("title", "entity.version-series.title")
                .put("description", "entity.version-series.description")
                .putObject("properties");

        ObjectNode artifactProperties = properties
                .putObject("version-artifact")
                .put("type", "object")
                .put("title", "entity.version-artifact.title")
                .put("description", "entity.version-artifact.description")
                .putObject("properties");

        ObjectNode seriesUiOptions = uiSchema.putObject("version-series");
        ObjectNode artifactUiOptions = uiSchema.putObject("version-artifact");

        applyVersionSeriesProperties(seriesProperties, seriesUiOptions);
        applyDatasetProperties(seriesProperties, seriesUiOptions);
        applyArtifactProperties(artifactProperties, artifactUiOptions);
        applyDatasetProperties(artifactProperties, artifactUiOptions);

        properties.get("version-series").asObject().putArray("required").add("title");
        properties.get("version-artifact").asObject().putArray("required").add("title");

        return new JsonForm(scheme, uiSchema, null);
    }

    private final ObjectMapper objectMapper;

    private final JsonForm jsonForm;

    public ArtifactReviewService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonForm = makeJsonForm(objectMapper);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        Objects.requireNonNull(context);
        final ObjectNode formData = objectMapper.createObjectNode();
        final ObjectNode series = formData.putObject("version-series");
        final ObjectNode artifact = formData.putObject("version-artifact");

        series.put(
                VersionSeries_.ontologyURI.getName(),
                context.getVersionSeries().getOntologyURI().toString());
        putDatasetFormData(context.getVersionSeries(), series);

        artifact.put(
                VersionArtifact_.versionUri.getName(),
                context.getVersionArtifact().getVersionUri().toString());
        artifact.put(
                VersionArtifact_.version.getName(), context.getVersionArtifact().getVersion());
        putDatasetFormData(context.getVersionArtifact(), artifact);

        return jsonForm.withFormData(formData);
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        return null;
    }

    private void putDatasetFormData(Dataset<?, ?> dataset, ObjectNode formData) {
        formData.set(Dataset_.title.getName(), objectMapper.valueToTree(dataset.getTitle()));
        formData.set(Dataset_.description.getName(), objectMapper.valueToTree(dataset.getDescription()));
    }
}
