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
    public static ImportProcessContext create(URI databaseContext) {
        final UUID uuid = UUID.randomUUID();
        try {
            final Path folder = Files.createTempDirectory("OntoPuS-import-process-" + uuid + "_");
            folder.toFile().deleteOnExit();
            return new ImportProcessContext(uuid, databaseContext, folder);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    private final UUID uuid;
    private final URI databaseContext;
    private final Path tempFolder;
    private final OntologyArtifact ontologyArtifact;

    private final Map<Object, Object> additionalProperties;

    protected ImportProcessContext(UUID uuid, URI databaseContext, Path tempFolder) {
        this.uuid = uuid;
        this.databaseContext = databaseContext;
        this.tempFolder = tempFolder;
        this.ontologyArtifact = new OntologyArtifact();
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
