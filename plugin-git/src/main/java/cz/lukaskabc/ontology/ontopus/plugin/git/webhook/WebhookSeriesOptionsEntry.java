package cz.lukaskabc.ontology.ontopus.plugin.git.webhook;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.api.util.VersionSeriesOptionsEntry;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.plugin.git.GitPlugin;
import cz.lukaskabc.ontology.ontopus.plugin.git.model.WebhookEntry;
import cz.lukaskabc.ontology.ontopus.plugin.git.persistence.service.WebhookEntryService;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class WebhookSeriesOptionsEntry implements VersionSeriesOptionsEntry {
    private static final String SCHEMA_BASE_NAME = WebhookSettingsRequest.class.getSimpleName();

    private static JsonForm makeJsonForm() {
        final JsonNode schema = JsonResourceLoader.loadJsonSchema(GitPlugin.FORM_RESOURCE_PATH, SCHEMA_BASE_NAME);
        final JsonNode uiSchema = JsonResourceLoader.loadUiSchema(GitPlugin.FORM_RESOURCE_PATH, SCHEMA_BASE_NAME);
        return new JsonForm(schema, uiSchema, null);
    }

    private final ObjectMapper objectMapper;
    private final WebhookEntryService webhookEntryService;
    private final JsonForm jsonForm;

    public WebhookSeriesOptionsEntry(ObjectMapper objectMapper, WebhookEntryService webhookEntryService) {
        this.webhookEntryService = webhookEntryService;
        this.jsonForm = makeJsonForm();
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonForm getForm(VersionSeriesURI seriesIdentifier) {
        return this.jsonForm.withFormData(loadCurrentData(seriesIdentifier));
    }

    @Override
    public String getIdentifier() {
        return "plugin-git-webhook";
    }

    @Override
    public String getLabel() {
        return "ontopus.plugin.git.webhook.settings.label";
    }

    @Override
    public void handleSubmit(
            VersionSeriesURI artifactIdentifier, FormJsonDataDto formData, MultiValueMap<String, MultipartFile> files) {
        WebhookSettingsRequest request =
                objectMapper.convertValue(formData.asObjectNode(objectMapper), WebhookSettingsRequest.class);
        for (WebhookEntry webhook : request.webhooks()) {
            verifyAndSetIdentifier(webhook, artifactIdentifier);
            if (webhook.getIdentifier() == null) {
                webhookEntryService.persist(webhook);
            } else {
                webhookEntryService.update(webhook);
            }
        }
    }

    private JsonNode loadCurrentData(VersionSeriesURI versionSeriesURI) {
        final List<WebhookEntry> webhooks = webhookEntryService.findAll(versionSeriesURI);
        final WebhookSettingsRequest request = new WebhookSettingsRequest(webhooks);
        return objectMapper.valueToTree(request);
    }

    @Override
    public boolean showMenuEntry(VersionSeriesURI artifactIdentifier) {
        return true;
    }

    private void verifyAndSetIdentifier(WebhookEntry entry, VersionSeriesURI versionSeriesURI) {
        if (entry.getVersionSeries() == null) {
            entry.setVersionSeries(versionSeriesURI);
        } else if (!entry.getVersionSeries().equals(versionSeriesURI)) {
            throw new IllegalArgumentException("Webhook entry version series does not match the artifact identifier");
        }
    }
}
