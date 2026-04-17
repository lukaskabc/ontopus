package cz.lukaskabc.ontology.ontopus.plugin.git.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

public class GitException extends OntopusException {

    public static GitExceptionBuilderStages.InternalMessageBuildStage builder() {
        return GitExceptionBuilderStages.start()
                .statusCode(HttpStatus.BAD_REQUEST)
                .errorType(Vocabulary.u_i_git);
    }

    @org.immutables.builder.Builder.Constructor
    public GitException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            String titleMessageCode,
            @Nullable Throwable cause,
            String detailMessageCode,
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
