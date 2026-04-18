package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;

/**
 * Indicates that an operation on import process was called because of a different task already being scheduled or in
 * progress
 */
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Different import task already in progress")
public class ImportProcessTaskConflictException extends OntopusException {
    public static ImportProcessTaskConflictException build() {
        return ImportProcessTaskConflictExceptionBuilderStages.start()
                .statusCode(HttpStatus.CONFLICT)
                .errorType(Vocabulary.u_i_task_conflict)
                .internalMessage("Another import task is already in progress")
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .titleMessageCode("ontopus.core.error.failedToSubmitTask")
                .detailMessageCode("ontopus.core.error.importProcessTaskConflict")
                .build();
    }

    @org.immutables.builder.Builder.Constructor
    public ImportProcessTaskConflictException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            @Nullable String titleMessageCode,
            @Nullable Throwable cause,
            @Nullable String detailMessageCode,
            Object[] detailMessageArguments) {
        super(
                statusCode,
                errorType,
                internalMessage,
                titleMessageCode,
                cause,
                detailMessageCode,
                detailMessageArguments);
    }
}
