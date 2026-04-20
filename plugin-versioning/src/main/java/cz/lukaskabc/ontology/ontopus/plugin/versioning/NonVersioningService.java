package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;

/** Uses constant version for the ontology {@code "latest"}, use the ontology URI for the version IRI */
public class NonVersioningService implements OntologyVersioningService {
    private static final String TRANSLATION_ROOT =
            "ontopus.core.service.OntologyVersioningService.NonVersioningService";
    /**
     * Provides a JSON form which will be shown to the user.
     *
     * @param context The import process context. Contents should not be modified.
     * @param previousFormData The data submitted in the previous import process of the ontology version series.
     * @return Form with JSON scheme and an optional UI Scheme
     * @implSpec The method can be called multiple times during the process execution, the result should be cached when
     *     possible.
     */
    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    /**
     * Provides information about actions of this service. How it versions the ontology.
     *
     * @return i18n translation key for the service name
     */
    @Override
    public String getServiceName() {
        return TRANSLATION_ROOT + ".title";
    }

    /**
     * Sets the version and version URI of the {@link VersionArtifact ontology artifact}.
     *
     * @param formResult The result of the submitted form
     * @param context The context of importing process
     */
    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        return null;
    }
}
