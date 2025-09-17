package cz.lukaskabc.ontology.ontopus.core.model;

import cz.lukaskabc.ontology.ontopus.core.model.util.SerializableImportProcessContext;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public class CombinedImportProcessInput extends SerializableImportProcessContext {
    private Map<String, MultipartFile> submittedFiles;

    public Map<String, MultipartFile> getSubmittedFiles() {
        return submittedFiles;
    }

    public void setSubmittedFiles(Map<String, MultipartFile> submittedFiles) {
        this.submittedFiles = submittedFiles;
    }
}
