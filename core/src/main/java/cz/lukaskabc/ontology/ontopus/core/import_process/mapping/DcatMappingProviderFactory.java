package cz.lukaskabc.ontology.ontopus.core.import_process.mapping;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProvider;
import cz.lukaskabc.ontology.ontopus.api.service.ArtifactPropertyMappingProviderFactory;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyURI;
import org.springframework.stereotype.Service;
// TODO move all mapping to a mapping plugin

@Service
public class DcatMappingProviderFactory implements ArtifactPropertyMappingProviderFactory {
    private final EntityManager entityManager;

    public DcatMappingProviderFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ArtifactPropertyMappingProvider getProvider(ImportProcessContext context, OntologyURI ontologyUri) {
        return new DcatPropertyMappingProvider(entityManager, ontologyUri, context.getTemporaryDatabaseContext());
    }
}
