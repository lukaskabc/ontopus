package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

public class VersionURIConstructionException extends OntopusException {

    public static VersionURIConstructionExceptionBuilderStages.InternalMessageBuildStage builder() {
        return VersionURIConstructionExceptionBuilderStages.start()
                .statusCode(HttpStatus.BAD_REQUEST)
                .errorType(Vocabulary.u_i_version_uri_construction);
    }

    @org.immutables.builder.Builder.Constructor
    public VersionURIConstructionException(
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
