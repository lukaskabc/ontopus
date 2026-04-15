package cz.lukaskabc.ontology.ontopus.core_model.exception;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

/** Exception from the Ontology Publication Server */
@NullMarked
public class OntopusCheckedException extends ErrorResponseException {
    public static final String TYPE_NAMESPACE = Vocabulary.ONTOLOGY_IRI_ONTOPUS + "/exception/";

    private final String titleMessageCode;
    private final String internalMessage;

    public OntopusCheckedException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            @Nullable String titleMessageCode,
            @Nullable String detailMessageCode,
            Object @Nullable [] detailMessageArguments,
            @Nullable Throwable cause) {
        super(statusCode, ProblemDetail.forStatus(statusCode), cause, detailMessageCode, detailMessageArguments);
        setType(errorType);
        this.internalMessage = internalMessage;
        this.titleMessageCode =
                titleMessageCode != null ? titleMessageCode : ErrorResponse.getDefaultTitleMessageCode(this.getClass());
    }

    public OntopusCheckedException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            @Nullable String titleMessageCode,
            @Nullable Throwable cause) {
        this(statusCode, errorType, internalMessage, titleMessageCode, null, null, cause);
    }

    public OntopusCheckedException(
            HttpStatusCode statusCode, URI errorType, String internalMessage, @Nullable Throwable cause) {
        this(statusCode, errorType, internalMessage, null, null, null, cause);
    }

    @Override
    public String getMessage() {
        return this.getStatusCode() + ", " + internalMessage
                + (!this.getHeaders().isEmpty() ? ", headers=" + this.getHeaders() : "") + ", " + this.getBody();
    }

    @Override
    public String getTitleMessageCode() {
        return titleMessageCode;
    }
}
