package cz.lukaskabc.ontology.ontopus.core.exception;

import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.net.URI;

/** Indicates that an operation on import process was called but the import process was not initialized yet. */
public class ImportProcessNotInitializedException extends OntopusException {
    public static final ImportProcessNotInitializedException INSTANCE =
            ImportProcessNotInitializedExceptionBuilderStages.start()
                    .statusCode(HttpStatus.RESET_CONTENT)
                    .errorType(Vocabulary.u_i_not_initialized)
                    .internalMessage("Import process not initialized")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .titleMessageCode("ontopus.core.error.importProcessNotInitialized")
                    .build();

    @org.immutables.builder.Builder.Constructor
    public ImportProcessNotInitializedException(
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

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
