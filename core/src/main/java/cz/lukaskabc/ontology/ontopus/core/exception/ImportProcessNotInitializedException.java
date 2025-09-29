package cz.lukaskabc.ontology.ontopus.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Indicates that an operation on import process was called but the import process was not initialized yet. */
@ResponseStatus(code = HttpStatus.RESET_CONTENT, reason = "Import process not initialized")
public class ImportProcessNotInitializedException extends IllegalStateException {}
