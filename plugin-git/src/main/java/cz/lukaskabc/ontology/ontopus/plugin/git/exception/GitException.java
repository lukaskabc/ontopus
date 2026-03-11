package cz.lukaskabc.ontology.ontopus.plugin.git.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;

public class GitException extends OntopusException {
    public GitException(String message) {
        super(message);
    }

    public GitException(String message, Throwable cause) {
        super(message, cause);
    }
}
