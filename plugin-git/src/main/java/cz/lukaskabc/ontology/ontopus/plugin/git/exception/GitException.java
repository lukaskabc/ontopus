package cz.lukaskabc.ontology.ontopus.plugin.git.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class GitException extends OntopusException {
    public static final URI TYPE = URI.create(TYPE_NAMESPACE + "git");

    public GitException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, TYPE, message, cause);
    }
}
