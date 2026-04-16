package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

public class OntopusSecurityException extends OntopusException {

    /** @see OntopusSecurityExceptionBuilder#OntopusSecurityExceptionBuilder() */
    public static OntopusSecurityExceptionBuilderStages.ErrorTypeBuildStage builder() {
        return OntopusSecurityExceptionBuilderStages.start().statusCode(HttpStatus.FORBIDDEN);
    }

    @org.immutables.builder.Builder.Constructor
    public OntopusSecurityException(
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
