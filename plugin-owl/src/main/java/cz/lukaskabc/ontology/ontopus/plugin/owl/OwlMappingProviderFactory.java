package cz.lukaskabc.ontology.ontopus.plugin.owl;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import org.springframework.stereotype.Service;

@Service
public class OwlMappingProviderFactory implements ArtifactPropertyMappingProviderFactory {
    private final EntityManager entityManager;

    public OwlMappingProviderFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ArtifactPropertyMappingProvider getProvider(ImportProcessContext context) {
        final VersionArtifactURI artifactURI = context.getVersionArtifact().getIdentifier();
        return new OwlPropertyMappingProvider(entityManager, artifactURI.toURI(), context.getDatabaseContext());
    }
}
