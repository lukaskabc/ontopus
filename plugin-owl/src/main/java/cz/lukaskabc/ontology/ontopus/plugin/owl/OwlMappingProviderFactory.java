package cz.lukaskabc.ontology.ontopus.plugin.owl;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class OwlMappingProviderFactory implements ArtifactPropertyMappingProviderFactory {
    private final EntityManager entityManager;

    public OwlMappingProviderFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ArtifactPropertyMappingProvider getProvider(ImportProcessContext context) {
        final URI artifactURI = context.getVersionArtifact().getUri();
        return new OwlPropertyMappingProvider(entityManager, artifactURI, context.getDatabaseContext());
    }
}
