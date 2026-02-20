package cz.lukaskabc.ontology.ontopus.core.rest.request;

import cz.lukaskabc.ontology.ontopus.core.rest.dto.FormDataDto;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

public class ImportProcessContextRequest implements Serializable {
    @NotNull private URI versionSeriesURI;

    @NotEmpty private List<String> servicesList;

    @NotNull private HashMap<String, FormDataDto> serviceToFormResultMap;

    public HashMap<String, FormDataDto> getServiceToFormResultMap() {
        return serviceToFormResultMap;
    }

    public List<String> getServicesList() {
        return servicesList;
    }

    public VersionSeriesURI getVersionSeriesURI() {
        return new VersionSeriesURI(versionSeriesURI);
    }

    public void setServiceToFormResultMap(HashMap<String, FormDataDto> serviceToFormResultMap) {
        this.serviceToFormResultMap = serviceToFormResultMap;
    }

    public void setServicesList(List<String> servicesList) {
        this.servicesList = servicesList;
    }

    public void setVersionSeriesURI(VersionSeriesURI versionSeriesURI) {
        this.versionSeriesURI = versionSeriesURI.toURI();
    }
}
