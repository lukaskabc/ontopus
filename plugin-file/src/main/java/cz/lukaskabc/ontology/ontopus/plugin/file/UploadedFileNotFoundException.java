package cz.lukaskabc.ontology.ontopus.plugin.file;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;

/** Indicates that an uploaded file was not found in the file system. */
public class UploadedFileNotFoundException extends OntopusException {
    public UploadedFileNotFoundException(String message) {
        super(message);
    }
}
