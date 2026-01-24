package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.core.model.VersionSeries;

/**
 * Factory capable of constructing an {@link OntologyVersioningService} instance for the given {@link VersionSeries}.
 * For each import process, a new {@link OntologyVersioningService} will be created.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface OntologyVersioningServiceFactory {
    /**
     * Builds an instance of {@link OntologyVersioningService} for the given {@link VersionSeries}.
     *
     * @param versionSeries the version series. The series can be newly created with no members.
     * @return the constructed {@link OntologyVersioningService}
     */
    OntologyVersioningService build(VersionSeries versionSeries);
}
