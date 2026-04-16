package cz.lukaskabc.ontology.ontopus.api.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

/** Indicates a failure of JSON form submission */
public class JsonFormSubmitException extends OntopusException {

    public JsonFormSubmitException(String internalMessage) {
        this(internalMessage, null);
    }

    public JsonFormSubmitException(String internalMessage, @Nullable Throwable cause) {
        super(HttpStatus.BAD_REQUEST, TYPE, internalMessage, cause);
    }
}
