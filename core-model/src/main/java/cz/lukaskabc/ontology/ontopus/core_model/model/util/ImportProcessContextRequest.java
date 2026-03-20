package cz.lukaskabc.ontology.ontopus.core_model.model.util;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.jspecify.annotations.NullUnmarked;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;

@NullUnmarked
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
