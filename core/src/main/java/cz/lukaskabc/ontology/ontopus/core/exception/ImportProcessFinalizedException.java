package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;

import java.util.Objects;

public class ImportProcessFinalizedException extends RuntimeException {
    private final VersionSeriesURI versionSeriesURI;

    public ImportProcessFinalizedException(VersionSeriesURI versionSeriesURI) {
        this.versionSeriesURI = Objects.requireNonNull(versionSeriesURI);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public VersionSeriesURI getVersionSeriesURI() {
        return versionSeriesURI;
    }
}
