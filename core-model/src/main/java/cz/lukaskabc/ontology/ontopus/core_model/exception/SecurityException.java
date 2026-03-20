package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.FORBIDDEN, reason = "Forbidden")
public class SecurityException extends OntopusException {
    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
