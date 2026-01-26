package cz.lukaskabc.ontology.ontopus.core_model.exception;

public class PersistenceException extends OntopusException {
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
