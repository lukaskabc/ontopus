package cz.lukaskabc.ontology.ontopus.core.rest.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.UploadedFile;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.nio.file.Path;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.SIMPLE_NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = FormFileRequest.CLASS_JSON_PROPERTY)
public class FormFileRequest implements Serializable {
    static final String CLASS_JSON_PROPERTY = "class";

    @NotEmpty private String fileName;

    @NotEmpty private String path;

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UploadedFile toUploadedFile(Path fsPath) {
        return new UploadedFile(fileName, fsPath, path);
    }
}
