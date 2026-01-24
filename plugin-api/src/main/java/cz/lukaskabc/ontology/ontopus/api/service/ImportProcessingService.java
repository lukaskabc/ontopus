package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import org.jspecify.annotations.Nullable;

// TODO: Design pattern Strategy

/**
 * A service that is a part of the ontology importing process
 *
 * <p>The implementation must be deterministic and execution with the same parameters must result in same effects.
 */
public interface ImportProcessingService<R> {

    /**
     * Called once this service was inserted at the top of the stack.
     *
     * @param context The process context with service stack with this service at the top.
     */
    default void afterStackPush(ImportProcessContext context) {}

    /**
     * Provides a JSON form which will be shown to the user.
     *
     * @return Form with JSON scheme and an optional UI Scheme
     */
    @Nullable JsonForm getJsonForm();

    /**
     * Provides the description of the Service shown in the UI.
     *
     * @return i18n translation key for the service description
     */
    default String getServiceDescription() {
        return getServiceName() + ".description"; // TODO implement import service description
    }

    /**
     * Provides the name of the Service shown in the UI.
     *
     * @return i18n translation key for the service name
     */
    String getServiceName();

    /**
     * Provides a unique identifier of the service type (not instance).
     *
     * @return a unique identifier for the service type
     */
    default String getUniqueIdentifier() {
        return this.getClass().getName();
    }

    /**
     * Accepts and handles the result of submitted form. If the service does not provide a form, it returns {@code null}
     * from {@link #getJsonForm()} then this method will be called with an empty form result without users interaction.
     *
     * @param formResult The data submitted in the form
     * @param context The import process context
     * @return The result of the operation
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    R handleSubmit(FormResult formResult, ImportProcessContext context);
}
