package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VersionURIConstructionException extends OntopusException {
    public VersionURIConstructionException(String message) {
        super(message);
    }

    public VersionURIConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
