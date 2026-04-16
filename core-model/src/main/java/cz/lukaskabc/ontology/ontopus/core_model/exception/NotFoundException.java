package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

public class NotFoundException extends OntopusException {

    /** @see NotFoundExceptionBuilder#NotFoundExceptionBuilder() */
    public static NotFoundExceptionBuilder builder() {
        return NotFoundExceptionBuilderStages.start()
                .statusCode(HttpStatus.NOT_FOUND)
                .errorType(Vocabulary.u_i_not_found)
                .titleMessageCode("ontopus.core.error.notFound.title")
                .detailMessageArguments(EMPTY_ARGUMENTS);
    }

    @org.immutables.builder.Builder.Constructor
    public NotFoundException(
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
