package cz.lukaskabc.ontology.ontopus.plugin.alias;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.api.util.SettingsEntry;
import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;

@Component
public class AliasSettingsEntry implements SettingsEntry {
    private static JsonForm makeJsonForm() {
        final JsonNode schema =
                JsonResourceLoader.loadJsonSchema(URIAliasPlugin.FORMS_PATH, URIAliasMapping.class.getSimpleName());
        final JsonNode uiSchema =
                JsonResourceLoader.loadUiSchema(URIAliasPlugin.FORMS_PATH, URIAliasMapping.class.getSimpleName());
        return new JsonForm(schema, uiSchema, null);
    }

    private final JsonForm jsonForm;

    public AliasSettingsEntry() {
        this.jsonForm = makeJsonForm();
    }

    /**
     * Provides JSON form that will be shown to the user
     *
     * @return the JSON form
     */
    @Override
    public JsonForm getForm() {
        // TODO: request list of current mappings from database
        return makeJsonForm();
    }

    /**
     * Label of the menu entry.
     *
     * @return i18n key for the translation of the menu entry.
     */
    @Override
    public String getLabel() {
        return "URI aliases";
    }

    /**
     * Handles the data submitted to the form.
     *
     * @param formData
     * @param files
     */
    @Override
    public void handleSubmit(FormJsonDataDto formData, MultiValueMap<String, MultipartFile> files) {}
}
