package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.springframework.http.HttpStatus;

public class RestInvalidDataException extends OntopusException {

    public RestInvalidDataException(String internalMessage, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, TYPE, internalMessage, cause);
    }
}
