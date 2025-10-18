package cz.lukaskabc.ontology.ontopus.plugin.dcatmapper;

import cz.lukaskabc.ontology.ontopus.core.exception.OntopusException;

public class StandardArtifactBuildingServiceException extends OntopusException {
    public StandardArtifactBuildingServiceException(String message) {
        super(message);
    }

    public StandardArtifactBuildingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
