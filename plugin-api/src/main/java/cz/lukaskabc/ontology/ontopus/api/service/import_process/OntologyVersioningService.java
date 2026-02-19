package cz.lukaskabc.ontology.ontopus.api.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;

/**
 * Service capable of versioning an {@link VersionArtifact ontology artifact}.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface OntologyVersioningService extends ImportProcessingService<Void> {
    /**
     * Provides information about actions of this service. How it versions the ontology.
     *
     * @return i18n translation key for the service name
     */
    @Override
    String getServiceName();

    /**
     * Sets the version and version URI of the {@link VersionArtifact ontology artifact}.
     *
     * @param formResult The result of the submitted form
     * @param context The context of importing process
     */
    @Override
    Void handleSubmit(FormResult formResult, ImportProcessContext context);
}
