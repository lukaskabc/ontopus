package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class InternalException extends OntopusException {
    public static final URI TYPE = URI.create(TYPE_NAMESPACE + "internal");

    public InternalException(String internalMessage, @Nullable Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, TYPE, internalMessage, cause);
    }
}
