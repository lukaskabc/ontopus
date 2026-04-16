package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/** Indicates a failure in importing files into the database */
public class FileImportException extends OntopusException {

    /** @see FileImportExceptionBuilder#FileImportExceptionBuilder() */
    public static FileImportExceptionBuilderStages.ErrorTypeBuildStage builder() {
        return FileImportExceptionBuilderStages.start().statusCode(HttpStatus.BAD_REQUEST);
    }

    @org.immutables.builder.Builder.Constructor
    public FileImportException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            @Nullable String titleMessageCode,
            @Nullable Throwable cause,
            @Nullable String detailMessageCode,
            Object @Nullable [] detailMessageArguments) {
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
