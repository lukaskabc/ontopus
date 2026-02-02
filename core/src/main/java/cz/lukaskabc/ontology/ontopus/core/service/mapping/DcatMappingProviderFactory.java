package cz.lukaskabc.ontology.ontopus.core.service.mapping;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import org.springframework.stereotype.Service;

@Service
public class DcatMappingProviderFactory implements ArtifactPropertyMappingProviderFactory {
    private final EntityManager entityManager;

    public DcatMappingProviderFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ArtifactPropertyMappingProvider getProvider(ImportProcessContext context) {
        final VersionArtifactURI artifactURI = context.getVersionArtifact().getIdentifier();
        return new DcatPropertyMappingProvider(entityManager, artifactURI.toURI(), context.getDatabaseContext());
    }
}
