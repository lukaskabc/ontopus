package cz.lukaskabc.ontology.ontopus.core.exception;

public abstract class OntopusException extends RuntimeException {
    public OntopusException(String message) {
        super(message);
    }

    public OntopusException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntopusException(Throwable cause) {
        super(cause);
    }
}
