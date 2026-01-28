package cz.lukaskabc.ontology.ontopus.plugin.skos;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class SkosMappingProviderFactory implements ArtifactPropertyMappingProviderFactory {
    private final EntityManager entityManager;

    public SkosMappingProviderFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ArtifactPropertyMappingProvider getProvider(ImportProcessContext context) {
        final URI artifactURI = context.getVersionArtifact().getUri();
        return new SkosPropertyMappingProvider(entityManager, artifactURI, context.getDatabaseContext());
    }
}
