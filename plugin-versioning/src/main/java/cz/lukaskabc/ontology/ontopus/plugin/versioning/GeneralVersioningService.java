package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.VersionArtifactRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Objects;

@Service
public class GeneralVersioningService implements OntologyVersioningService {
    static final String VERSION_FIELD = "version";

    @Nullable private JsonForm jsonForm;

    private final ObjectMapper objectMapper;
    private final VersionArtifactRepository artifactRepository;

    public GeneralVersioningService(ObjectMapper objectMapper, VersionArtifactRepository artifactRepository) {
        this.objectMapper = objectMapper;
        this.artifactRepository = artifactRepository;
    }

    @Override
    public void afterStackPush(ImportProcessContext context) {
        String previousVersion = null;
        VersionArtifactURI latestVersionUri = context.getVersionSeries().getLast();
        if (latestVersionUri != null) {
            previousVersion = artifactRepository.findRequired(latestVersionUri).getVersion();
        }
        this.jsonForm = makeJsonForm(previousVersion);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context) {
        return jsonForm;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OntologyVersioningService.GeneralVersioningService.name";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        try {
            String version = formResult.getStringValue(VERSION_FIELD);
            Objects.requireNonNull(version);
            context.getVersionArtifact().setVersion(version);
        } catch (Exception e) {
            throw new OntopusException(e); // TODO exception
        }
        return null;
    }

    protected JsonForm makeJsonForm(@Nullable String previousVersion) {
        ObjectNode scheme = objectMapper.createObjectNode();
        scheme.put("type", "object");
        ObjectNode properties = scheme.putObject("properties");
        properties
                .putObject(VERSION_FIELD)
                .put("type", "string")
                .put(
                        "title",
                        "ontopus.core.service.OntologyVersioningService.GeneralVersioningService.field.version.title")
                .putArray("examples")
                .add(previousVersion);

        return new JsonForm(scheme, null, null);
    }
}
