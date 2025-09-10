package cz.lukaskabc.ontology.ontopus.api.model;

import java.io.File;
import java.net.URI;
import java.util.*;

public class ImportProcessContext {
    private final UUID uuid = UUID.randomUUID();
    private final URI databaseContext;
    private final Set<File> folders = new HashSet<>();
    private final Map<Object, Object> additionalProperties = new HashMap<>();

    public ImportProcessContext(URI databaseContext) {
        this.databaseContext = databaseContext;
    }

    public void addFolder(File folder) {
        if (folder.isDirectory()) {
            folders.add(folder);
        } else {
            throw new IllegalArgumentException("Not a directory");
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<File> getFolders() {
        return Collections.unmodifiableSet(folders);
    }

    public URI getDatabaseContext() {
        return databaseContext;
    }

    public Object getAdditionalProperty(Object key) {
        return additionalProperties.get(key);
    }

    public void setAdditionalProperty(Object key, Object value) {
        additionalProperties.put(key, value);
    }
}
