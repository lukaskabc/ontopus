package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;

/**
 * Service capable of versioning an {@link VersionArtifact ontology artifact}.
 *
 * @implSpec The service is constructed with {@link OntologyVersioningServiceFactory} for each import process.
 */
public interface OntologyVersioningService extends OntologyArtifactBuildingService {
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
