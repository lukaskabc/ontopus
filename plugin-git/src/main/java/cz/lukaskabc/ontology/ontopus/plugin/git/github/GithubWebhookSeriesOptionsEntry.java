package cz.lukaskabc.ontology.ontopus.plugin.git.github;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.api.util.VersionSeriesOptionsEntry;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GithubWebhook;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
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
    private final GithubWebhookService webhookService;

    public GithubWebhookSeriesOptionsEntry(ObjectMapper objectMapper, GithubWebhookService webhookService) {
        this.objectMapper = objectMapper;
        this.webhookService = webhookService;
        this.jsonForm = makeJsonForm();
    }

    @Override
    public JsonForm getForm(VersionSeriesURI entityIdentifier) {
        ObjectNode formData = objectMapper.createObjectNode();
        ArrayNode arrayWrapper = formData.putArray("webhook");
        webhookService
                .findByVersionSeries(entityIdentifier)
                .ifPresentOrElse(webhook -> arrayWrapper.add(objectMapper.valueToTree(webhook)), () -> {
                    arrayWrapper.addObject().put("secret", StringUtils.randomString(32));
                });

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
            VersionSeriesURI artifactIdentifier, FormJsonDataDto formData, MultiValueMap<String, MultipartFile> files) {
        final JsonNode webhookJson = resolveWebhookJson(formData);
        if (webhookJson == null) {
            webhookService.deleteByVersionSeries(artifactIdentifier);
            return;
        }

        final GithubWebhook webhook = objectMapper.convertValue(webhookJson, GithubWebhook.class);
        webhook.setVersionSeries(artifactIdentifier);
        webhookService.save(webhook);
    }

    @Nullable private JsonNode resolveWebhookJson(FormJsonDataDto formData) {
        JsonNode webhookJson = formData.get("webhook");
        if (webhookJson != null && webhookJson.isArray()) {
            return webhookJson.get(0);
        } else {
            throw new IllegalArgumentException("Expected 'webhook' to be an array with a single object");
        }
    }

    @Override
    public boolean showMenuEntry(VersionSeriesURI artifactIdentifier) {
        return true;
    }
}
