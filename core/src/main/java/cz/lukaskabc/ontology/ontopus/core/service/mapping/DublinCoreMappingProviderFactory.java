package cz.lukaskabc.ontology.ontopus.core.service.mapping;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class DublinCoreMappingProviderFactory implements ArtifactPropertyMappingProviderFactory {
    private final EntityManager entityManager;

    public DublinCoreMappingProviderFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ArtifactPropertyMappingProvider getProvider(ImportProcessContext context) {
        final URI artifactURI = context.getVersionArtifact().getUri();
        return new DublinCorePropertyMappingProvider(entityManager, artifactURI, context.getDatabaseContext());
    }
}
