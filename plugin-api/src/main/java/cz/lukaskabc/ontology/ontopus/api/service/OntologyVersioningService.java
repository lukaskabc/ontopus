package cz.lukaskabc.ontology.ontopus.api.service;

/**
 * Service capable of versioning an {@link cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact OntologyArtifact}.
 * The version can be retrieved from a user input or from the ontology data.
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
