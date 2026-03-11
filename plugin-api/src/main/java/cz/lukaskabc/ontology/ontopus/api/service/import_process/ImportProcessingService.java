package cz.lukaskabc.ontology.ontopus.api.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;

// TODO: Design pattern Strategy

/**
 * A service that is a part of the ontology importing process
 *
 * <p>The implementation must be deterministic and execution with the same parameters must result in same effects.
 *
 * @see #getUniqueContextIdentifier(ReadOnlyImportProcessContext) by default, the service can be in the import process
 *     stack only once
 */
public interface ImportProcessingService<R> {

    /**
     * Called once this service was inserted at the top of the stack.
     *
     * @implSpec The service should not store the context or any of its parts.
     * @param context The process context with service stack with this service at the top.
     */
    default void afterStackPush(ImportProcessContext context) {}

    /**
     * Provides a JSON form which will be shown to the user.
     *
     * @param context The import process context. Contents should not be modified.
     * @param previousFormData The data submitted in the previous import process of the ontology version series.
     * @implSpec The method can be called multiple times during the process execution, the result should be cached when
     *     possible.
     * @return Form with JSON scheme and an optional UI Scheme
     */
    @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData);

    /**
     * Provides the description of the Service shown in the UI.
     *
     * @return i18n translation key for the service description
     * @implNote The default implementation replaces the last part of the i18n key with "description".
     */
    default String getServiceDescription() {
        final int lastDot = getServiceName().lastIndexOf(".");
        return getServiceName().substring(0, lastDot) + ".description"; // TODO implement import service description
    }

    /**
     * Provides the name of the Service shown in the UI.
     *
     * @return i18n translation key for the service name
     */
    String getServiceName();

    /**
     * Provides a unique identifier for the service instance in the context of the import process. The identifier must
     * be unique for each invocation of the service in the import process. The identifier must be stable between
     * publishing a new ontology and publishing a new version of the same ontology.
     *
     * @implSpec The service is guaranteed to be at the top of the service stack when this method is called.
     * @param context The import process context with this service at the top of the stack.
     * @return a unique identifier for the service instance in the context of the import process
     */
    default String getUniqueContextIdentifier(ReadOnlyImportProcessContext context) {
        return this.getClass().getName();
    }

    /**
     * Accepts and handles the result of submitted form. If the service does not provide a form, it returns {@code null}
     * from {@link #getJsonForm(ReadOnlyImportProcessContext, JsonNode)} then this method will be called with an empty
     * form result without users interaction.
     *
     * @param formResult The data submitted in the form
     * @param context The import process context
     * @return The result of the operation
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    R handleSubmit(FormResult formResult, ImportProcessContext context);
}
