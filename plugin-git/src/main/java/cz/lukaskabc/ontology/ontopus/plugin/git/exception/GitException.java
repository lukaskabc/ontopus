package cz.lukaskabc.ontology.ontopus.plugin.git.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.springframework.http.HttpStatus;

public class GitException extends OntopusException {

    public GitException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, TYPE, message, cause);
    }
}
