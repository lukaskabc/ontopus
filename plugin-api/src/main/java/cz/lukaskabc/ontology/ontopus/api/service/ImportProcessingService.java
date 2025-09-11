package cz.lukaskabc.ontology.ontopus.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import org.jspecify.annotations.Nullable;

// TODO: Design pattern Strategy

/** A service that is a part of the ontology importing process */
public interface ImportProcessingService<R> {
    /**
     * Provides the name of the Service shown in the UI
     *
     * @return i18n translation key for the service name
     */
    String getServiceName();

    /**
     * Provides form schema shown to the user to enter data.
     *
     * @return JSON schema of the form
     * @see #getUiSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/json-schema/">RJSF JSON schema</a>
     * @see <a href= "https://json-schema.org/draft-07/json-schema-release-notes">JSON schema Draft 7</a>
     */
    @Nullable default JsonNode getFormSchema() {
        return null;
    }
    ;

    /**
     * Provides UI Schema of the form shown to the user.
     *
     * @return UI schema of the form
     * @see #getFormSchema()
     * @see <a href= "https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema">RJSF UI Schema</a>
     */
    @Nullable default JsonNode getUiSchema() {
        return null;
    }

    /**
     * Accepts and handles the result of submitted form.
     *
     * @param formResult The data submitted in the form
     * @param context The import process context
     * @return The result of the operation and
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    Result<R> handleSubmit(FormResult formResult, ImportProcessContext context);

    /**
     * A result of a {@link ImportProcessingService} action
     *
     * @param value The result value
     * @param nextService The next processing service to call
     * @param <R> The type of the resulting value
     */
    record Result<R>(@Nullable R value, @Nullable ImportProcessingService<?> nextService) {}
}
