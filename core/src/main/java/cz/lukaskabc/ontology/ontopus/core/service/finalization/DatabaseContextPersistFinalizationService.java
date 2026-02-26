package cz.lukaskabc.ontology.ontopus.core.service.finalization;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportFinalizingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DatabaseContextPersistFinalizationService implements ImportFinalizingService {

    private final GraphService graphService;

    public DatabaseContextPersistFinalizationService(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public void finalizeImport(ImportProcessContext context) {
        VersionArtifactURI ontologyGraph = Objects.requireNonNull(
                context.getVersionArtifact().getIdentifier(), "Version artifact URI must not be null");
        graphService.move(context.getDatabaseContext(), ontologyGraph.toGraphURI());
    }
}
