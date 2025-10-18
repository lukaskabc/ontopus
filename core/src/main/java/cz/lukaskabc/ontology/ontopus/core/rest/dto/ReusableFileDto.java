package cz.lukaskabc.ontology.ontopus.core.rest.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.model.ReusableFile;
import jakarta.validation.constraints.NotNull;
import java.io.File;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.SIMPLE_NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = ReusableFileDto.CLASS_JSON_PROPERTY)
public class ReusableFileDto {
    static final String CLASS_JSON_PROPERTY = "class";

    public static boolean matches(JsonNode node) {
        if (node != null && node.isObject()) {
            final JsonNode clazz = node.get(CLASS_JSON_PROPERTY);
            if (clazz != null && clazz.isTextual()) {
                return ReusableFileDto.class.getSimpleName().equals(clazz.asText());
            }
        }
        return false;
    }

    @NotNull private ReusableFile.Type type;

    @NotNull private String formFieldName;

    @NotNull private String fileName;

    public String getFileName() {
        return fileName;
    }

    public String getFormFieldName() {
        return formFieldName;
    }

    public ReusableFile.Type getType() {
        return type;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFormFieldName(String formFieldName) {
        this.formFieldName = formFieldName;
    }

    public void setType(ReusableFile.Type type) {
        this.type = type;
    }

    public ReusableFile toReusableFile(File file) {
        return new ReusableFile(type, formFieldName, fileName, file);
    }
}
