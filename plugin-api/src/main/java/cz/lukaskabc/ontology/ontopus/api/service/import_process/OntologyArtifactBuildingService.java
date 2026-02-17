package cz.lukaskabc.ontology.ontopus.api.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact;

/**
 * Service capable of (partially) building {@link VersionArtifact OntologyArtifact} either from user input or from the
 * Ontology data in database.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface OntologyArtifactBuildingService extends ImportProcessingService<Void> {

    /**
     * Provides information about actions of this service, should indicate what ontologies are supported and what this
     * service does.
     *
     * @return i18n translation key for the service name
     */
    @Override
    String getServiceName();

    /**
     * Sets data to partially built ontology artifact.
     *
     * @param formResult The result of the submitted form
     * @param context The context of importing process
     * @return Result with {@code null} value
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    @Override
    Void handleSubmit(FormResult formResult, ImportProcessContext context);
}
