package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.springframework.http.HttpStatus;

import java.net.URI;

public class NotAcceptableException extends OntopusException {
    public static final URI TYPE = URI.create(TYPE_NAMESPACE + "not-acceptable");

    public NotAcceptableException(String internalMessage) {
        super(HttpStatus.NOT_ACCEPTABLE, TYPE, internalMessage, null);
    }
}
