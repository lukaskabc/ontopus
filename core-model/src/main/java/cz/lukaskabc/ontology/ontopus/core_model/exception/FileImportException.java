package cz.lukaskabc.ontology.ontopus.core_model.exception;

/** Indicates a failure in importing files into the database */
public class FileImportException extends OntopusException {
    public FileImportException(String message) {
        super(message);
    }
}
