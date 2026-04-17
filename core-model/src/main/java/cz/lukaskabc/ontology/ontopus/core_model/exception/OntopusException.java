package cz.lukaskabc.ontology.ontopus.core_model.exception;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

/**
 * Exception from the Ontology Publication Server
 *
 * @see OntopusCheckedException
 */
@NullMarked
public abstract class OntopusException extends ErrorResponseException {
    public static final Object[] EMPTY_ARGUMENTS = new Object[0];

    private final String titleMessageCode;
    private final String internalMessage;

    public OntopusException(
            HttpStatusCode statusCode,
            URI errorType,
            String internalMessage,
            @Nullable String titleMessageCode,
            @Nullable Throwable cause,
            @Nullable String detailMessageCode,
            Object[] detailMessageArguments) {
        super(statusCode, ProblemDetail.forStatus(statusCode), cause, detailMessageCode, detailMessageArguments);
        setType(errorType);
        this.internalMessage = internalMessage;
        this.titleMessageCode =
                titleMessageCode != null ? titleMessageCode : ErrorResponse.getDefaultTitleMessageCode(this.getClass());
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
