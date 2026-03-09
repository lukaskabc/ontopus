package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;

/**
 * Constructs {@link ArtifactPropertyMappingProvider} for the given import context.
 *
 * @implSpec Must be registered in Spring context (e.g. with {@link org.springframework.stereotype.Service @Service}
 *     annotation)
 */
public interface ArtifactPropertyMappingProviderFactory {
    /**
     * Constructs {@link ArtifactPropertyMappingProvider} for the given import context.
     *
     * @param context the import process context
     * @return the provider constructed for the given context
     */
    ArtifactPropertyMappingProvider getProvider(ImportProcessContext context, OntologyURI ontologyURI);
}
