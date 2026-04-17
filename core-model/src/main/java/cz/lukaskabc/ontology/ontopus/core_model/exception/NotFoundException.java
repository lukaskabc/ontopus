package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

public class NotFoundException extends OntopusException {

    /** @see NotFoundExceptionBuilder#NotFoundExceptionBuilder() */
    public static NotFoundExceptionBuilderStages.InternalMessageBuildStage builder() {
        return NotFoundExceptionBuilderStages.start()
                .statusCode(HttpStatus.NOT_FOUND)
                .errorType(Vocabulary.u_i_not_found);
    }

    @org.immutables.builder.Builder.Constructor
    public NotFoundException(
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
