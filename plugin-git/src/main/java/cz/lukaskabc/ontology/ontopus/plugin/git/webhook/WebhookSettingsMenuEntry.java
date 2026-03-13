package cz.lukaskabc.ontology.ontopus.plugin.git.webhook;

import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.SettingsEntry;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Component
public class WebhookSettingsMenuEntry implements SettingsEntry {
    private final JsonForm jsonForm;
    private final ObjectMapper objectMapper;

    public WebhookSettingsMenuEntry(JsonForm jsonForm, ObjectMapper objectMapper) {
        this.jsonForm = jsonForm;
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonForm getForm() {
        return makeJsonForm();
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
    public void handleSubmit(FormResult formResult) {}

    protected JsonForm makeJsonForm() {
        final ObjectNode schema = objectMapper.createObjectNode();
        final ObjectNode uiSchema = objectMapper.createObjectNode();

        return new JsonForm(schema, uiSchema, null);
    }
}
