package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImportProcessContext {
    private final UUID uuid;
    private final URI databaseContext;
    private final Path tempFolder;
    private final OntologyArtifact ontologyArtifact;

    private final Map<Object, Object> additionalProperties;

    public ImportProcessContext(UUID uuid, URI databaseContext, Path tempFolder, OntologyArtifact ontologyArtifact) {
        this.uuid = uuid;
        this.databaseContext = databaseContext;
        this.tempFolder = tempFolder;
        this.ontologyArtifact = ontologyArtifact;
        this.additionalProperties = new HashMap<>();
    }

    public Object getAdditionalProperty(Object key) {
        return additionalProperties.get(key);
    }

    public URI getDatabaseContext() {
        return databaseContext;
    }

    public OntologyArtifact getOntologyArtifact() {
        return ontologyArtifact;
    }

    public Path getTempFolder() {
        return tempFolder;
    }

    public Path getTempFolder(Path relativePath) {
        try {
            return Files.createDirectory(tempFolder.resolve(relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setAdditionalProperty(Object key, Object value) {
        additionalProperties.put(key, value);
    }
}
