package cz.lukaskabc.ontology.ontopus.core.rest.request;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;

public class ImportProcessContextRequest implements Serializable {
    @NotNull private URI versionSeriesURI;

    @Valid @NotNull private SerializableImportProcessContext serializableImportProcessContext;

    public SerializableImportProcessContext getSerializableImportProcessContext() {
        return serializableImportProcessContext;
    }

    public VersionSeriesURI getVersionSeriesURI() {
        return new VersionSeriesURI(versionSeriesURI);
    }

    public void setSerializableImportProcessContext(SerializableImportProcessContext serializableImportProcessContext) {
        this.serializableImportProcessContext = serializableImportProcessContext;
    }

    public void setVersionSeriesURI(VersionSeriesURI versionSeriesURI) {
        this.versionSeriesURI = versionSeriesURI.toURI();
    }
}
