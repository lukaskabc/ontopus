package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.api.util.VersionSeriesOptionsEntry;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Component
public class GithubWebhookSeriesOptionsEntry implements VersionSeriesOptionsEntry {
    private static final String SCHEMA_BASE_NAME = GithubWebhook.class.getSimpleName();

    private static JsonForm makeJsonForm() {
        final JsonNode schema = JsonResourceLoader.loadJsonSchema(GitPlugin.FORM_RESOURCE_PATH, SCHEMA_BASE_NAME);
        final JsonNode uiSchema = JsonResourceLoader.loadUiSchema(GitPlugin.FORM_RESOURCE_PATH, SCHEMA_BASE_NAME);
        return new JsonForm(schema, uiSchema, null);
    }

    private final ObjectMapper objectMapper;

    private final JsonForm jsonForm;

    public GithubWebhookSeriesOptionsEntry(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonForm = makeJsonForm();
    }

    @Override
    public JsonForm getForm(VersionSeriesURI entityIdentifier) {
        ObjectNode formData = objectMapper.createObjectNode();
        formData.putObject("webhook").put("secret", StringUtils.randomString(32));
        return jsonForm.withFormData(formData);
    }

    @Override
    public String getIdentifier() {
        return "github-webhook";
    }

    @Override
    public String getLabel() {
        return "ontopus.plugin.git.webhook.github.label";
    }

    @Override
    public void handleSubmit(
            VersionSeriesURI artifactIdentifier,
            FormJsonDataDto formData,
            MultiValueMap<String, MultipartFile> files) {}

    @Override
    public boolean showMenuEntry(VersionSeriesURI artifactIdentifier) {
        return true;
    }
}
