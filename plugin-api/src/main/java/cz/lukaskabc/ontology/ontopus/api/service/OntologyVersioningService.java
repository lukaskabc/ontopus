package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;

/**
 * Service capable of versioning an {@link VersionArtifact OntologyArtifact}. The version can be retrieved from a user
 * input or from the ontology data.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface OntologyVersioningService extends OntologyArtifactBuildingService {
    /**
     * Provides information about actions of this service. How it versions the ontology.
     *
     * @return i18n translation key for the service name
     */
    @Override
    String getServiceName();
}
