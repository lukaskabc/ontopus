package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.Objects;
import java.util.stream.Stream;

@Service
public class GeneralVersioningService implements OntologyVersioningService {
    static final String VERSION_FIELD = "version";

    private final ObjectMapper objectMapper;

    public GeneralVersioningService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        String lastVersion = context.getVersionSeries().getVersion();
        String resolvedVersion = context.getVersionArtifact().getVersion();

        ObjectNode scheme = objectMapper.createObjectNode();
        scheme.put("type", "object");
        ObjectNode properties = scheme.putObject("properties");
        ObjectNode versionField = properties
                .putObject(VERSION_FIELD)
                .put("type", "string")
                .put(
                        "title",
                        "ontopus.core.service.OntologyVersioningService.GeneralVersioningService.field.version.title");
        ArrayNode examples = versionField.putArray("examples");

        Stream.of(lastVersion, resolvedVersion).filter(Objects::nonNull).forEach(examples::add);

        ObjectNode formData = null;
        if (resolvedVersion != null) {
            formData = objectMapper.createObjectNode();
            formData.put(VERSION_FIELD, resolvedVersion);
        } else if (previousFormData != null && previousFormData.isObject()) {
            formData = (ObjectNode) previousFormData;
        }

        return new JsonForm(scheme, null, formData);
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
}
