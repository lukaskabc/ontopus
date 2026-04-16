package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

public class ValidationException extends OntopusException {

    private static String orDefaultMessage(@Nullable String message) {
        if (message == null) {
            return "Object validation failed";
        }
        return message;
    }

    public ValidationException(@Nullable String message) {
        super(HttpStatus.BAD_REQUEST, TYPE, orDefaultMessage(message), null);
    }
}
