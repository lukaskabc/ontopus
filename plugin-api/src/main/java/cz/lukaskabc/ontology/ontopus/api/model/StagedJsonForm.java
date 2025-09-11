package cz.lukaskabc.ontology.ontopus.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class StagedJsonForm extends JsonForm {
    private static final String ABSOLUTE_REST_PATH_REGEX = "(\\/[A-Za-z0-9_.\\~-]+)+";

    @Pattern(regexp = ABSOLUTE_REST_PATH_REGEX) @NotEmpty private final String submitPath;

    public StagedJsonForm(
            JsonNode jsonSchema, @Nullable JsonNode uiSchema, String submitPath, @Nullable String nextFormPath) {
        super(jsonSchema, uiSchema);
        this.submitPath = submitPath;
    }

    public String getSubmitPath() {
        return submitPath;
    }
}
