package cz.lukaskabc.ontology.ontopus.core.rest.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.UploadedFile;
import tools.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.nio.file.Path;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.SIMPLE_NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = FormFileRequest.CLASS_JSON_PROPERTY)
public class FormFileRequest implements Serializable {
    static final String CLASS_JSON_PROPERTY = "class";

    public static boolean matches(JsonNode node) {
        if (node != null && node.isObject()) {
            final JsonNode clazz = node.get(CLASS_JSON_PROPERTY);
            if (clazz != null && clazz.isString()) {
                return FormFileRequest.class.getSimpleName().equals(clazz.asString());
            }
        }
        return false;
    }

    @NotEmpty private String fileName;

    @NotEmpty private String path;

    @NotEmpty private String formFieldName;

    public String getFileName() {
        return fileName;
    }

    public String getFormFieldName() {
        return formFieldName;
    }

    public String getPath() {
        return path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFormFieldName(String formFieldName) {
        this.formFieldName = formFieldName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UploadedFile toUploadedFile(Path fsPath) {
        return new UploadedFile(fileName, fsPath, path);
    }
}
