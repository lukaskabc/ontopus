package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

@ResponseStatus(HttpStatus.CREATED)
public class ImportProcessFinalizedException extends RuntimeException {
    private final VersionSeriesURI versionSeriesURI;

    public ImportProcessFinalizedException(VersionSeriesURI versionSeriesURI) {
        this.versionSeriesURI = Objects.requireNonNull(versionSeriesURI);
    }

    public VersionSeriesURI getVersionSeriesURI() {
        return versionSeriesURI;
    }
}
