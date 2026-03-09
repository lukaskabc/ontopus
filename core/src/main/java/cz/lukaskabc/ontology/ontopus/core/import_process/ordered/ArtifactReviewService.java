package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Objects;

@Service
@Order(ImportProcessServiceOrder.ARTIFACT_REVIEW_SERVICE)
public class ArtifactReviewService implements OrderedImportPipelineService<Void> {
    private static String any(MultilingualString multi) {
        if (multi == null) return "";
        if (multi.contains(null)) return multi.get();
        Iterator<String> langIt = multi.getLanguages().iterator();
        if (langIt.hasNext()) {
            return multi.get(langIt.next());
        }
        return "";
    }

    private static void loadArtifactProperties(ObjectNode properties, ReadOnlyImportProcessContext context) {
        final VersionArtifact artifact = context.getVersionArtifact();
        properties
                .putObject(VersionArtifact_.identifier.getName())
                .put("type", "string")
                .put("default", Objects.toString(artifact.getIdentifier()))
                .put("disabled", true);
        properties
                .putObject(VersionArtifact_.title.getName())
                .put("type", "string")
                .put("default", any(artifact.getTitle())); // TODO: multilingual string in
        // JSON form?
        properties
                .putObject(VersionArtifact_.description.getName())
                .put("type", "string")
                .put("default", any(artifact.getDescription()));
        properties
                .putObject(VersionArtifact_.version.getName())
                .put("type", "string")
                .put("default", artifact.getVersion());
    }

    private static void loadVersionSeriesProperties(ObjectNode properties, ReadOnlyImportProcessContext context) {
        final VersionSeries series = context.getVersionSeries();
        properties
                .putObject(VersionSeries_.identifier.getName())
                .put("type", "string")
                .put("default", Objects.toString(series.getIdentifier()))
                .put("disabled", true);
        properties
                .putObject(VersionSeries_.title.getName())
                .put("type", "string")
                .put("default", any(series.getTitle())); // TODO: multilingual string in
        // JSON form?
        properties
                .putObject(VersionSeries_.description.getName())
                .put("type", "string")
                .put("default", any(series.getDescription()));
    }

    private final ObjectMapper objectMapper;

    public ArtifactReviewService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        Objects.requireNonNull(context);
        ObjectNode scheme = objectMapper.createObjectNode();
        scheme.put("type", "object");
        ObjectNode properties = scheme.putObject("properties");
        ObjectNode seriesProperties = properties
                .putObject("series")
                .put("type", "object")
                .put("title", "ontopus.core.service.ArtifactReviewService.series.title")
                .put("description", "ontopus.core.service.ArtifactReviewService.series.description")
                .putObject("properties");
        ObjectNode artifactProperties = properties
                .putObject("artifact")
                .put("type", "object")
                .put("title", "ontopus.core.service.ArtifactReviewService.artifact.title")
                .put("description", "ontopus.core.service.ArtifactReviewService.artifact.description")
                .putObject("properties");

        loadVersionSeriesProperties(seriesProperties, context);
        loadArtifactProperties(artifactProperties, context);

        return new JsonForm(scheme, null, null);
    }

    @Override
    public String getServiceName() {
        return "";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        return null;
    }
}
