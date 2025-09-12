package cz.lukaskabc.ontology.ontopus.core.factory;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.service.core.TemporaryContextGenerator;
import cz.lukaskabc.ontology.ontopus.core.model.ImportProcess;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import cz.lukaskabc.ontology.ontopus.core.service.OntologyArtifactService;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImportProcessFactory {
    static Path createTempFolder(UUID uuid) {
        try {
            final Path folder = Files.createTempDirectory("OntoPuS-import-process-" + uuid + "_");
            folder.toFile().deleteOnExit();
            return folder;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    private final OntologyArtifactService ontologyArtifactService;

    private final TemporaryContextGenerator contextGenerator;

    @Autowired
    public ImportProcessFactory(
            OntologyArtifactService ontologyArtifactService, TemporaryContextGenerator contextGenerator) {
        this.ontologyArtifactService = ontologyArtifactService;
        this.contextGenerator = contextGenerator;
    }

    public ImportProcess create() {
        final UUID uuid = UUID.randomUUID();
        final Path tempFolder = createTempFolder(uuid);
        final URI databaseContext = this.contextGenerator.generate();
        final OntologyArtifact artifact = new OntologyArtifact();

        final ImportProcessContext context = new ImportProcessContext(uuid, databaseContext, tempFolder, artifact);
        return new ImportProcess(context, getProcessingServices());
    }

    protected Stack<ImportProcessingService<?>> getProcessingServices() {
        Stack<ImportProcessingService<?>> stack = new Stack<>();
        return stack;
    }
}
