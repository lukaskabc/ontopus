package cz.lukaskabc.ontology.ontopus.plugin.widoco.exception;

import org.springframework.core.NestedCheckedException;

public class WidocoExecutionException extends NestedCheckedException {
    public WidocoExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
