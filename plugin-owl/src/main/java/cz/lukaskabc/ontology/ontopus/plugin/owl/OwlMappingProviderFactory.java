package cz.lukaskabc.ontology.ontopus.plugin.owl;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import org.springframework.stereotype.Service;

@Service
public class OwlMappingProviderFactory implements ArtifactPropertyMappingProviderFactory {
    private final EntityManager entityManager;

    public OwlMappingProviderFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ArtifactPropertyMappingProvider getProvider(ImportProcessContext context, OntologyURI ontologyUri) {
        return new OwlPropertyMappingProvider(entityManager, ontologyUri, context.getTemporaryDatabaseContext());
    }
}
