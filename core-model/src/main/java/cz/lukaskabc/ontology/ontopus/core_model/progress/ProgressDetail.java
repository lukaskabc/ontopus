package cz.lukaskabc.ontology.ontopus.core_model.progress;

import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.net.URI;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Represents the current progress of an asynchronous action. The progress is serialized as {@link ErrorResponse} */
@NullMarked
public class ProgressDetail implements ErrorResponse {
    public static final String TYPE_NAMESPACE = Vocabulary.ONTOLOGY_IRI_ONTOPUS + "/progress/";
    public static final String PROGRESS_PROPERTY = "currentProgressValue";
    public static final String MAX_PROGRESS_PROPERTY = "maxProgressValue";
    private final ProblemDetail problemDetail;
    private final HttpStatusCode statusCode;

    private String titleMessageCode;
    private String detailMessageCode;
    private Object @Nullable [] detailMessageArguments;

    private final AtomicInteger currentProgressValue = new AtomicInteger(0);
    private int maxProgressValue = -1;
    private final AtomicReference<ProgressState> state = new AtomicReference<>(ProgressState.SCHEDULED);

    public ProgressDetail(HttpStatusCode statusCode, URI type, String defaultMessage) {
        this.statusCode = statusCode;
        this.problemDetail = ProblemDetail.forStatusAndDetail(statusCode, defaultMessage);
        this.problemDetail.setType(type);
        this.titleMessageCode = ErrorResponse.getDefaultTitleMessageCode(this.getClass());
        this.detailMessageCode = ErrorResponse.getDefaultDetailMessageCode(this.getClass(), null);
    }

    /**
     * Constructs {@link ProgressDetail} with default {@link HttpStatus#CONFLICT} status code.
     *
     * @param type the type of the progress
     * @param defaultMessage the default message
     */
    public ProgressDetail(URI type, String defaultMessage) {
        this(HttpStatus.CONFLICT, type, defaultMessage);
    }

    /**
     * Return the body for the response, formatted as an RFC 9457 {@link ProblemDetail} whose
     * {@link ProblemDetail#getStatus() status} should match the response status.
     *
     * <p><strong>Note:</strong> The returned {@code ProblemDetail} may be updated before the response is rendered, for
     * example, via {@link #updateAndGetBody(MessageSource, Locale)}. Therefore, implementing methods should use an
     * instance field, and should not re-create the {@code ProblemDetail} on every call, nor use a static variable.
     */
    @Override
    public ProblemDetail getBody() {
        return problemDetail;
    }

    public int getCurrentProgressValue() {
        return currentProgressValue.get();
    }

    @Override
    public Object @Nullable [] getDetailMessageArguments() {
        return detailMessageArguments;
    }

    @Override
    public String getDetailMessageCode() {
        return detailMessageCode;
    }

    public int getMaxProgressValue() {
        return maxProgressValue;
    }

    /** Return the HTTP status code to use for the response. */
    @Override
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public String getTitleMessageCode() {
        return titleMessageCode;
    }

    public ProgressDetail setDetailMessageArguments(Object @Nullable [] detailMessageArguments) {
        this.detailMessageArguments = detailMessageArguments;
        return this;
    }

    public ProgressDetail setDetailMessageCode(String detailMessageCode) {
        this.detailMessageCode = detailMessageCode;
        return this;
    }

    public ProgressDetail setMaxProgressValue(int maxProgressValue) {
        this.maxProgressValue = maxProgressValue;
        return this;
    }

    public ProgressDetail setTitleMessageCode(String titleMessageCode) {
        this.titleMessageCode = titleMessageCode;
        return this;
    }

    /**
     * Use the given {@link MessageSource} to resolve the {@link #getTitleMessageCode() title}, and
     * {@link #getDetailMessageCode() detail} message codes, and then use the resolved values to update the
     * corresponding fields in {@link #getBody()}.
     *
     * @param messageSource the {@code MessageSource} to use for the lookup
     * @param locale the {@code Locale} to use for the lookup
     */
    public ProblemDetail updateAndGetBody(@Nullable MessageSource messageSource, Locale locale) {
        if (messageSource != null) {
            Object[] arguments = getDetailMessageArguments(messageSource, locale);
            String detail = messageSource.getMessage(getDetailMessageCode(), arguments, null, locale);
            if (detail != null) {
                getBody().setDetail(detail);
            }
            String title = messageSource.getMessage(getTitleMessageCode(), null, null, locale);
            if (title != null) {
                getBody().setTitle(title);
            }
        }
        getBody().setProperty(PROGRESS_PROPERTY, getCurrentProgressValue());
        getBody().setProperty(MAX_PROGRESS_PROPERTY, getMaxProgressValue());
        return getBody();
    }

    public ProgressDetail withProgressValue(int progressValue) {
        currentProgressValue.set(progressValue);
        return this;
    }

    public ProgressDetail withState(ProgressState newState) {
        this.state.set(newState);
        return this;
    }
}
