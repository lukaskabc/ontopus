package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.jspecify.annotations.NullUnmarked;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;

/** Holds list of processed services with corresponding submitted forms for them. */
@NullUnmarked
public class SerializableImportProcessContext implements Serializable {
    @NotNull private URI versionSeriesUri;

    @NotEmpty private List<String> servicesList;

    @NotNull private Map<String, FormDataDto> serviceToFormResultMap;

    public SerializableImportProcessContext() {}

    public Map<String, FormDataDto> getServiceToFormResultMap() {
        return serviceToFormResultMap;
    }

    public List<String> getServicesList() {
        return servicesList;
    }

    public VersionSeriesURI getVersionSeriesUri() {
        return new VersionSeriesURI(versionSeriesUri);
    }

    public void setServiceToFormResultMap(Map<String, FormDataDto> serviceToFormResultMap) {
        this.serviceToFormResultMap = serviceToFormResultMap;
    }

    public void setServicesList(List<String> servicesList) {
        this.servicesList = servicesList;
    }

    public void setVersionSeriesUri(VersionSeriesURI versionSeriesUri) {
        this.versionSeriesUri = versionSeriesUri.toURI();
    }
}
