package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;

public class InitializationException extends InternalException {
    public InitializationException(String internalMessage, @Nullable Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                Vocabulary.u_i_initialization,
                internalMessage,
                null,
                cause,
                null,
                EMPTY_ARGUMENTS);
    }
}
