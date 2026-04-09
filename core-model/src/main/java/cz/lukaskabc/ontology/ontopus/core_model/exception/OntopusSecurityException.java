package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = org.springframework.http.HttpStatus.FORBIDDEN, reason = "Forbidden")
public class OntopusSecurityException extends OntopusException {
    public OntopusSecurityException(String message) {
        super(message);
    }

    public OntopusSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
