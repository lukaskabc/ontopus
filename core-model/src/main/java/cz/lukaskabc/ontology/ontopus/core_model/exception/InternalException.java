package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

public class InternalException extends OntopusException {

    /** @see InternalExceptionBuilder#InternalExceptionBuilder() */
    public static InternalExceptionBuilderStages.ErrorTypeBuildStage builder() {
        return InternalExceptionBuilderStages.start().statusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.immutables.builder.Builder.Constructor
    public InternalException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            @Nullable String titleMessageCode,
            @Nullable Throwable cause,
            @Nullable String detailMessageCode,
            Object @Nullable ... detailMessageArguments) {
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
