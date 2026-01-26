package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;

public class ValidationException extends OntopusException {
    private static String orDefaultMessage(@Nullable String message) {
        if (message == null) {
            return "Object validation failed";
        }
        return message;
    }

    public ValidationException(@Nullable String message) {
        super(orDefaultMessage(message));
    }
}
