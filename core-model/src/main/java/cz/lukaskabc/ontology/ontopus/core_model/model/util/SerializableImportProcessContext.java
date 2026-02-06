package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Holds list of processed services with corresponding submitted forms for them. */
public class SerializableImportProcessContext implements Serializable {
    @NotNull private URI versionSeriesIdentifier;
    // TODO: make a DTO/Request object without files directory, that will be
    // retrieved from version series
    @NotEmpty private String filesDirectory;

    @NotEmpty private List<String> servicesList;

    @NotNull private HashMap<String, ReusableFormResult> serviceToReusableFormResultMap;

    /**
     * @param formData
     * @param reusableFiles
     */
    public record ReusableFormResult(Map<String, String> formData, Map<String, UploadedFile> reusableFiles)
            implements Serializable {
        // TODO implement factory using relative paths for backed up files available for
        // reusage
    }

    public SerializableImportProcessContext() {}

    public URI getVersionSeriesIdentifier() {
        return versionSeriesIdentifier;
    }

    public void setVersionSeriesIdentifier(URI versionSeriesIdentifier) {
        this.versionSeriesIdentifier = versionSeriesIdentifier;
    }

    public String getFilesDirectory() {
        return filesDirectory;
    }

    public void setFilesDirectory(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    public List<String> getServicesList() {
        return servicesList;
    }

    public void setServicesList(List<String> servicesList) {
        this.servicesList = servicesList;
    }

    public HashMap<String, ReusableFormResult> getServiceToReusableFormResultMap() {
        return serviceToReusableFormResultMap;
    }

    public void setServiceToReusableFormResultMap(HashMap<String, ReusableFormResult> serviceToReusableFormResultMap) {
        this.serviceToReusableFormResultMap = serviceToReusableFormResultMap;
    }
}
