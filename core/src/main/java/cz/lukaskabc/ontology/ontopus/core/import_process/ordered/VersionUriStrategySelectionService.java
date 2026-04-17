package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ResultHandlingServiceWrapper;
import cz.lukaskabc.ontology.ontopus.core.service.process.VersionURIConstructionService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

/** Automatically picks the version URI construction strategy based on available data */
@Service
@Order(ImportProcessServiceOrder.VERSION_URI_STRATEGY_SELECTION_SERVICE)
public class VersionUriStrategySelectionService implements OrderedImportPipelineService<Void> {

    private static void setVersionUri(OntologyVersionURI versionUri, ImportProcessContext context) {
        context.getVersionArtifact().setVersionUri(versionUri);
    }

    private final VersionURIConstructionService versionURIConstructionService;

    public VersionUriStrategySelectionService(VersionURIConstructionService versionURIConstructionService) {
        this.versionURIConstructionService = versionURIConstructionService;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Nullable private OntologyVersionURI getVersionUri(ReadOnlyImportProcessContext context) {
        return context.getVersionArtifact().getVersionUri();
    }

    /**
     * Accepts and handles the result of submitted form. If the service does not provide a form, it returns {@code null}
     * from {@link #getJsonForm(ReadOnlyImportProcessContext, JsonNode)} then this method will be called with an empty
     * form result without users interaction.
     *
     * @param formResult The data submitted in the form
     * @param context The import process context
     * @return The result of the operation
     * @throws JsonFormSubmitException when the submitted form data is invalid. When the exception is thrown, the form
     *     result won't be saved in the context and the service will remain on the stack.
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        if (isVersionUriSet(context)) {
            return null; // version URI is already set
        }

        final ResultHandlingServiceWrapper<?> wrapper = new ResultHandlingServiceWrapper<>(
                versionURIConstructionService, VersionUriStrategySelectionService::setVersionUri);
        if (context.peekService() != this) {
            throw new IllegalStateException("Unexpected import process service stack state");
        }
        context.popService(); // pop self
        context.pushService(wrapper);
        return null;
    }

    private boolean isVersionUriSet(ReadOnlyImportProcessContext context) {
        return getVersionUri(context) != null;
    }
}
