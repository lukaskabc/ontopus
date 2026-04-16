package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

public class PersistenceException extends OntopusException {

    public static PersistenceExceptionBuilderStages.InternalMessageBuildStage builder() {
        return PersistenceExceptionBuilderStages.start()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorType(Vocabulary.u_i_persistence);
    }

    @org.immutables.builder.Builder.Constructor
    public PersistenceException(
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
