package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.springframework.http.HttpStatus;

import java.net.URI;

/** Indicates a failure in importing files into the database */
public class FileImportException extends OntopusException {
    public static final URI TYPE = URI.create(TYPE_NAMESPACE + "file-import");

    public FileImportException(String message) {
        super(HttpStatus.BAD_REQUEST, TYPE, message, null);
    }
}
