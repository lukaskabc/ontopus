package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;

/**
 * Service capable of publishing an {@link VersionArtifact OntologyArtifact} via a public endpoint.
 *
 * <p>The service is triggered once {@link VersionArtifact OntologyArtifact} is fully constructed. The service can
 * request input from user and do any necessary pre-processing in order to publish the artifact (e.g. generating static
 * files).
 *
 * <p>If a service does not need an input from the user, handling
 * {@link cz.lukaskabc.ontology.ontopus.api.event.OntologyArtifactCreated OntologyArtifactCreated} event can be used
 * instead.
 */
public interface OntologyPublishingService extends ImportProcessingService<Void> {

    /**
     * Provides information about actions of this service. How the ontology will be published (e.g. in which format)
     *
     * @return i18n translation key for the service name
     */
    @Override
    String getServiceName();

    /**
     * Sets data to the partially built ontology artifact in the context.
     *
     * @param formResult The result of the submitted form
     * @param context The context of importing process
     * @return Result with {@code null} value
     * @implSpec The caller is responsible for invoking this method asynchronously if blocking operation is not desired.
     */
    @Override
    Result<Void> handleSubmit(FormResult formResult, ImportProcessContext context);
}
