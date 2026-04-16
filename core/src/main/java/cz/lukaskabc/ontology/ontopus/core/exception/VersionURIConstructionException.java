package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.springframework.http.HttpStatus;

public class VersionURIConstructionException extends OntopusException {

    public VersionURIConstructionException(String internalMessage) {
        super(HttpStatus.BAD_REQUEST, TYPE, internalMessage, null);
    }
}
