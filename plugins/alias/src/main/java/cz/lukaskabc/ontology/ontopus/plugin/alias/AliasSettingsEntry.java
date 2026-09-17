package cz.lukaskabc.ontology.ontopus.plugin.alias;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.util.JsonResourceLoader;
import cz.lukaskabc.ontology.ontopus.api.util.SettingsEntry;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service.AliasService;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.type.CollectionType;

import java.util.Set;

@Component
public class AliasSettingsEntry implements SettingsEntry {
    private static JsonForm makeJsonForm() {
        final JsonNode schema =
                JsonResourceLoader.loadJsonSchema(URIAliasPlugin.FORMS_PATH, URIAliasMapping.class.getSimpleName());
        final JsonNode uiSchema =
                JsonResourceLoader.loadUiSchema(URIAliasPlugin.FORMS_PATH, URIAliasMapping.class.getSimpleName());
        return new JsonForm(schema, uiSchema, null);
    }

    private final ObjectMapper objectMapper;

    private final JsonForm jsonForm;
    private final AliasService aliasService;

    public AliasSettingsEntry(AliasService aliasService, ObjectMapper objectMapper) {
        this.aliasService = aliasService;
        this.jsonForm = makeJsonForm();
        this.objectMapper = objectMapper;
    }

    /**
     * Provides JSON form that will be shown to the user
     *
     * @return the JSON form
     */
    @Override
    public JsonForm getForm() {
        final ObjectNode formData = objectMapper.createObjectNode();
        final ArrayNode configuredAliases = formData.putArray("configuredAliases");
        final ArrayNode aliases = formData.putArray("aliases");

        aliasService.getStaticAliases().forEach((resource, alias) -> {
            configuredAliases.addObject().put("resource", resource.toString()).put("alias", alias.toString());
        });

        aliasService.findAllMappings().forEach(mapping -> {
            aliases.addObject()
                    .put("resource", mapping.getResource().toString())
                    .put("alias", mapping.getAlias().toString());
        });

        return jsonForm.withFormData(formData);
    }

    /**
     * Label of the menu entry.
     *
     * @return i18n key for the translation of the menu entry.
     */
    @Override
    public String getLabel() {
        return "ontopus.plugin.alias.uri-alias-mapping.title";
    }

    private Set<URIAliasMapping> getMappings(FormJsonDataDto formData) {
        JsonNode aliasesNode = formData.get("aliases");
        if (aliasesNode == null || !aliasesNode.isArray()) {
            throw JsonFormSubmitException.missingValue("aliases");
        }

        CollectionType typeRef =
                objectMapper.getTypeFactory().constructCollectionType(Set.class, URIAliasMapping.class);

        Set<URIAliasMapping> mappings = objectMapper.treeToValue(aliasesNode, typeRef);
        if (mappings == null) {
            throw JsonFormSubmitException.missingValue("aliases");
        }

        return mappings;
    }

    /**
     * Handles the data submitted to the form.
     *
     * @param formData
     * @param files
     */
    @Override
    public void handleSubmit(FormJsonDataDto formData, MultiValueMap<String, MultipartFile> files) {
        Set<URIAliasMapping> mappings = getMappings(formData);
        aliasService.replaceAll(mappings);
    }
}
