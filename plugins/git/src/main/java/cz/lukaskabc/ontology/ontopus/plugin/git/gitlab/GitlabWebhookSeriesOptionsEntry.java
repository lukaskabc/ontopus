package cz.lukaskabc.ontology.ontopus.plugin.git.gitlab;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.api.util.VersionSeriesOptionsEntry;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.GitlabWebhook;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Component
public class GitlabWebhookSeriesOptionsEntry implements VersionSeriesOptionsEntry {
    /** Endpoint field of the JSON schema */
    private static final JsonPointer ENDPOINT = JsonPointer.compile("/properties/webhook/items/properties/endpoint");

    private static JsonForm makeJsonForm() {
        return new JsonForm(
                JsonResourceLoader.loadJsonSchema(GitPlugin.FORM_RESOURCE_PATH, GitlabWebhook.class.getSimpleName()),
                JsonResourceLoader.loadUiSchema(GitPlugin.FORM_RESOURCE_PATH, GitlabWebhook.class.getSimpleName()),
                null);
    }

    private final ObjectMapper objectMapper;
    private final JsonForm jsonForm;
    private final GitlabWebhookService service;
    private final UriComponents webhookUrl;

    private final VersionSeriesService versionSeriesService;

    public GitlabWebhookSeriesOptionsEntry(
            ObjectMapper objectMapper,
            GitlabWebhookService service,
            OntopusConfig config,
            VersionSeriesService versionSeriesService) {
        this.objectMapper = objectMapper;
        this.service = service;
        this.versionSeriesService = versionSeriesService;
        this.jsonForm = makeJsonForm();
        this.webhookUrl = UriComponentsBuilder.fromUri(config.getSystemUri())
                .path(GitlabWebhookController.PATH)
                .queryParam("series", "{series}")
                .build();
    }

    @Override
    public JsonForm getForm(VersionSeriesURI series) {
        final JsonNode jsonSchema = jsonForm.getJsonSchema();
        final JsonNode uiSchema = jsonForm.getUiSchema();

        final ObjectNode formData = objectMapper.createObjectNode();
        final ArrayNode arrayWrapper = formData.putArray("webhook");

        // ensure version series exists
        versionSeriesService.findRequiredById(series);

        service.findByVersionSeries(series).ifPresent(webhook -> arrayWrapper.add(objectMapper.valueToTree(webhook)));

        String url = webhookUrl.expand(series).encode().toUriString();
        jsonSchema.at(ENDPOINT).asObject().put("const", url);

        return new JsonForm(jsonSchema, uiSchema, formData);
    }

    @Override
    public String getIdentifier() {
        return "gitlab-webhook";
    }

    @Override
    public String getLabel() {
        return "ontopus.plugin.git.webhook.gitlab.label";
    }

    @Override
    public void handleSubmit(
            VersionSeriesURI series, FormJsonDataDto formData, MultiValueMap<String, MultipartFile> files) {
        JsonNode webhookJson = resolveWebhookJson(formData);
        if (webhookJson == null) {
            service.deleteByVersionSeries(series);
            return;
        }
        GitlabWebhook webhook = objectMapper.convertValue(webhookJson, GitlabWebhook.class);
        webhook.setVersionSeries(series);
        service.save(webhook);
    }

    @Nullable private JsonNode resolveWebhookJson(FormJsonDataDto formData) {
        JsonNode webhook = formData.get("webhook");
        if (webhook != null && webhook.isArray()) {
            return webhook.get(0);
        }
        throw ValidationException.fromValidationError("Expected 'webhook' to be an array with a single object");
    }

    @Override
    public boolean showMenuEntry(VersionSeriesURI series) {
        return true;
    }
}
