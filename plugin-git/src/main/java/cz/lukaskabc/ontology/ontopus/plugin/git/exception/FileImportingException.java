package cz.lukaskabc.ontology.ontopus.plugin.git.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;

public class FileImportingException extends OntopusException {
    public FileImportingException(String message, Throwable cause) {
        super(message, cause);
    }
}
