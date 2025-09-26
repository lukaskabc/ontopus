package cz.lukaskabc.ontology.ontopus.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates that an operation on import process was called because of a different task already being scheduled or in
 * progress
 */
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Different import task already in progress")
public class ImportProcessTaskConflictException extends IllegalStateException {}
