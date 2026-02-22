package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RestInvalidDataException extends OntopusException {
    public RestInvalidDataException(String message) {
        super(message);
    }

    public RestInvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
