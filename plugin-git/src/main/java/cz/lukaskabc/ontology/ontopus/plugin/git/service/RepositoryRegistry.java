package cz.lukaskabc.ontology.ontopus.plugin.git.service;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class RepositoryRegistry {
    private final Map<UUID, Future<File>> repositories = new ConcurrentHashMap<>(5);

    @Nullable public Future<File> lookup(UUID repositoryId) {
        return repositories.get(repositoryId);
    }

    public void register(UUID repositoryId, Future<File> cloningFuture) {
        repositories.put(repositoryId, cloningFuture);
    }
}
