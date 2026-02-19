package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

/** Holds list of processed services with corresponding submitted forms for them. */
public class SerializableImportProcessContext implements Serializable {
    @NotNull private URI versionSeriesIdentifier;
    // TODO: make a DTO/Request object without files directory, that will be
    // retrieved from version series
    @NotEmpty private String filesDirectory;

    @NotEmpty private List<String> servicesList;

    @NotNull private HashMap<String, FormResult> serviceToReusableFormResultMap;

    public SerializableImportProcessContext() {}

    public String getFilesDirectory() {
        return filesDirectory;
    }

    public HashMap<String, FormResult> getServiceToReusableFormResultMap() {
        return serviceToReusableFormResultMap;
    }

    public List<String> getServicesList() {
        return servicesList;
    }

    public URI getVersionSeriesIdentifier() {
        return versionSeriesIdentifier;
    }

    public void setFilesDirectory(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    public void setServiceToReusableFormResultMap(HashMap<String, FormResult> serviceToReusableFormResultMap) {
        this.serviceToReusableFormResultMap = serviceToReusableFormResultMap;
    }

    public void setServicesList(List<String> servicesList) {
        this.servicesList = servicesList;
    }

    public void setVersionSeriesIdentifier(URI versionSeriesIdentifier) {
        this.versionSeriesIdentifier = versionSeriesIdentifier;
    }
}
