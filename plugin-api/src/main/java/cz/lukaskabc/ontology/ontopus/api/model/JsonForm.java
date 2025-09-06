package cz.lukaskabc.ontology.ontopus.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class JsonForm {

    /** @see <a href= "https://json-schema.org/draft-07/json-schema-release-notes">JSON schema 7</a> */
    private final JsonNode jsonSchema;

    @Nullable private final JsonNode uiSchema;

    public JsonForm(JsonNode jsonSchema, @Nullable JsonNode uiSchema) {
        this.jsonSchema = jsonSchema;
        this.uiSchema = uiSchema;
    }

    public JsonNode getJsonSchema() {
        return jsonSchema;
    }

    public @Nullable JsonNode getUiSchema() {
        return uiSchema;
    }
}
