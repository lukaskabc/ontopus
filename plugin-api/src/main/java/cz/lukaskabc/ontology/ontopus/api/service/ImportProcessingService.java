package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
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
     * Provides a JSON form which will be shown to the user.
     *
     * @return Form with JSON scheme and an optional UI Scheme
     */
    @Nullable JsonForm getJsonForm();

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
