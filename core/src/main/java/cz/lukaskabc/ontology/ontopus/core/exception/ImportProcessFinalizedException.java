package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CREATED)
public class ImportProcessFinalizedException extends RuntimeException {
    private final VersionArtifactURI versionArtifactURI;

    public ImportProcessFinalizedException(VersionArtifactURI versionArtifactURI) {
        this.versionArtifactURI = versionArtifactURI;
    }

    public VersionArtifactURI getVersionArtifactURI() {
        return versionArtifactURI;
    }
}
