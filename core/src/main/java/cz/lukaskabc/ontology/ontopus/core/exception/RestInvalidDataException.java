package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class RestInvalidDataException extends OntopusException {
    public static final URI TYPE = URI.create(TYPE_NAMESPACE + "invalid-data");

    public RestInvalidDataException(String internalMessage, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, TYPE, internalMessage, cause);
    }
}
