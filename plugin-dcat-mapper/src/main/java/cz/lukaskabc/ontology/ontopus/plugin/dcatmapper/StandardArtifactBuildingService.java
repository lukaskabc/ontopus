package cz.lukaskabc.ontology.ontopus.plugin.dcatmapper;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyArtifactBuildingService;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import java.net.URI;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class StandardArtifactBuildingService implements OntologyArtifactBuildingService {
    private final EntityManager entityManager;

    public StandardArtifactBuildingService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return null;
        // TODO: services returning null for JSON form should be called automatically
    }

    @Override
    public String getServiceName() {
        return "ontopus.plugin.dcatmapper.StandardArtifactBuildingService.serviceName";
    }

    @Override
    public Void handleSubmit(FormResult unused, ImportProcessContext context) {
        final VersionArtifact artifact = context.getVersionArtifact();
        final TemporaryContextURI contextURI = context.getDatabaseContext();
        final URI ontologyURI = context.getVersionSeries().getOntologyIdentifier();

        if (ontologyURI == null) {
            throw new StandardArtifactBuildingServiceException("Ontology URI is null");
        }

        StandardDatasetMapper.mapAll(entityManager, ontologyURI, contextURI.toURI(), artifact);
        return null;
    }
}
