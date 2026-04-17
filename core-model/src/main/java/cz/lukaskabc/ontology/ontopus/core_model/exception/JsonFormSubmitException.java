package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/** Indicates a failure of JSON form submission */
public class JsonFormSubmitException extends OntopusCheckedException {
    public static JsonFormSubmitExceptionBuilderStages.ErrorTypeBuildStage builder() {
        return JsonFormSubmitExceptionBuilderStages.start().statusCode(HttpStatus.BAD_REQUEST);
    }

    @org.immutables.builder.Builder.Constructor
    public JsonFormSubmitException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            String titleMessageCode,
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
