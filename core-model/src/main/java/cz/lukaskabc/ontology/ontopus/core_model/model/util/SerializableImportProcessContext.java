package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import org.jspecify.annotations.NullUnmarked;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/** Holds list of processed services with corresponding submitted forms for them. */
@NullUnmarked
public class SerializableImportProcessContext implements Serializable {

    @NotNull private Map<String, FormDataDto> serviceToFormResultMap;

    public SerializableImportProcessContext() {}

    public Map<String, FormDataDto> getServiceToFormResultMap() {
        return serviceToFormResultMap;
    }

    public void setServiceToFormResultMap(Map<String, FormDataDto> serviceToFormResultMap) {
        this.serviceToFormResultMap = serviceToFormResultMap;
    }
}
