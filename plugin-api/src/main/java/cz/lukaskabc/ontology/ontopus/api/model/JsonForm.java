package cz.lukaskabc.ontology.ontopus.api.model;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;

@NullMarked
public class JsonForm {

    /**
     * Form schema shown to the user to enter data.
     *
     * @see #uiSchema
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/json-schema/">RJSF JSON schema</a>
     * @see <a href= "https://json-schema.org/draft-07/json-schema-release-notes">JSON schema Draft 7</a>
     */
    private final JsonNode jsonSchema;

    /**
     * Provides UI Schema of the form shown to the user.
     *
     * @see #jsonSchema
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema">RJSF UI Schema</a>
     */
    @Nullable private final JsonNode uiSchema;

    @Nullable private final JsonNode formData;

    /**
     * @param jsonSchema Form JSON schema. See {@link #jsonSchema}
     * @param uiSchema Form UI Schema. See {@link #uiSchema}
     */
    public JsonForm(JsonNode jsonSchema, @Nullable JsonNode uiSchema, @Nullable JsonNode formData) {
        this.jsonSchema = jsonSchema;
        this.uiSchema = uiSchema;
        this.formData = formData;
    }

    public @Nullable JsonNode getFormData() {
        if (formData == null) {
            return null;
        }
        return formData.deepCopy();
    }

    public JsonNode getJsonSchema() {
        return jsonSchema.deepCopy();
    }

    public @Nullable JsonNode getUiSchema() {
        if (uiSchema == null) {
            return null;
        }
        return uiSchema.deepCopy();
    }

    public JsonForm withFormData(@Nullable JsonNode formData) {
        return new JsonForm(this.jsonSchema, this.uiSchema, formData);
    }
}
