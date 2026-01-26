package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** Holds list of processed services with corresponding submitted forms for them. */
public class SerializableImportProcessContext implements Serializable {
    private String filesDirectory;
    private List<String> servicesList;
    private List<ReusableFormResult> formResults;

    /**
     * @param formData
     * @param reusableFiles
     */
    public record ReusableFormResult(Map<String, String[]> formData, Map<String, UploadedFile> reusableFiles)
            implements Serializable {
        // TODO implement factory using relative paths for backed up files available for
        // reusage
    }

    public List<String> getServicesList() {
        return servicesList;
    }

    public void setServicesList(List<String> servicesList) {
        this.servicesList = servicesList;
    }

    public List<ReusableFormResult> getFormResults() {
        return formResults;
    }

    public void setFormResults(List<ReusableFormResult> formResults) {
        this.formResults = formResults;
    }

    public String getFilesDirectory() {
        return filesDirectory;
    }

    public void setFilesDirectory(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }
}
