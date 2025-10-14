package cz.lukaskabc.ontology.ontopus.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Exception from the Ontology Publication Server */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OntopusException extends RuntimeException {
    public OntopusException(String message) {
        super(message);
    }

    public OntopusException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntopusException(Throwable cause) {
        super(cause);
    }
}
