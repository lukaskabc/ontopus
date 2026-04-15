package cz.lukaskabc.ontology.ontopus.plugin.file;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;

/** Indicates that an uploaded file was not found in the file system. */
public class UploadedFileNotFoundException extends JsonFormSubmitException {
    public UploadedFileNotFoundException(String message) {
        super(message);
    }
}
