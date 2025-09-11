package cz.lukaskabc.ontology.ontopus.api.model;

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
    private final Map<Object, Object> additionalProperties;

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

    protected ImportProcessContext(UUID uuid, URI databaseContext, Path tempFolder) {
        this.uuid = uuid;
        this.databaseContext = databaseContext;
        this.tempFolder = tempFolder;
        this.additionalProperties = new HashMap<>();
    }

    public Path getTempFolder(Path relativePath) {
        try {
            return Files.createDirectory(tempFolder.resolve(relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    public Object getAdditionalProperty(Object key) {
        return additionalProperties.get(key);
    }

    public URI getDatabaseContext() {
        return databaseContext;
    }

    public Path getTempFolder() {
        return tempFolder;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setAdditionalProperty(Object key, Object value) {
        additionalProperties.put(key, value);
    }
}
