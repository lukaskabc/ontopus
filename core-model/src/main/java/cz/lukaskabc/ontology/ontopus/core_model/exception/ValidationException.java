package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

public class ValidationException extends OntopusException {

    public static ValidationExceptionBuilderStages.InternalMessageBuildStage builder() {
        return ValidationExceptionBuilderStages.start()
                .statusCode(HttpStatus.BAD_REQUEST)
                .errorType(Vocabulary.u_i_validation);
    }

    public static ValidationException fromValidationError(String message) {
        return ValidationException.builder()
                .internalMessage(message)
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .build();
    }

    @org.immutables.builder.Builder.Constructor
    public ValidationException(
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
