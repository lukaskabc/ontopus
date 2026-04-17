package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.io.IOException;
import java.net.URI;

public class InternalException extends OntopusException {

    /** @see InternalExceptionBuilder#InternalExceptionBuilder() */
    public static InternalExceptionBuilderStages.ErrorTypeBuildStage builder() {
        return InternalExceptionBuilderStages.start().statusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static InternalException fileProcessingException(String internalMessage, @Nullable IOException e) {
        return InternalException.builder()
                .errorType(Vocabulary.u_i_file_processing)
                .internalMessage(internalMessage)
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .cause(e)
                .build();
    }

    public static InternalException serializationException(String internalMessage, Exception e) {
        return InternalException.builder()
                .errorType(Vocabulary.u_i_ontopus_serialization)
                .internalMessage(internalMessage)
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .cause(e)
                .build();
    }

    public static InternalException unexpectedServiceStackState() {
        return InternalException.builder()
                .errorType(Vocabulary.u_i_unknown)
                .internalMessage("Unexpected import process service stack state")
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .build();
    }

    @org.immutables.builder.Builder.Constructor
    public InternalException(
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
