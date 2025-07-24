package cz.lukaskabc.ontology.ontopus.core.exception;

import org.jspecify.annotations.Nullable;

public class ValidationException extends OntopusException {
    public ValidationException(@Nullable String message) {
        super(orDefaultMessage(message));
    }

    private static String orDefaultMessage(@Nullable String message) {
        if (message == null) {
            return "Object validation failed";
        }
        return message;
    }
}
