package cz.lukaskabc.ontology.ontopus.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Indicates an exception during import process operation */
@ResponseStatus(HttpStatus.CONFLICT)
public class ImportProcessException extends RuntimeException {
    public ImportProcessException(Throwable cause) {
        super(cause);
    }
}
