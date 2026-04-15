package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import org.springframework.http.HttpStatus;

import java.net.URI;

public class VersionURIConstructionException extends OntopusException {
    public static final URI TYPE = URI.create(TYPE_NAMESPACE + "version-uri-construction");

    public VersionURIConstructionException(String internalMessage) {
        super(HttpStatus.BAD_REQUEST, TYPE, internalMessage, null);
    }
}
