package cz.lukaskabc.ontology.ontopus.api.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/** Ensures that {@link #directories} exist and are writable and readable */
public abstract class DirectoryInitializationService implements InitializationService {
    public static final int ORDER = 500;
    private final Set<Path> directories;

    public DirectoryInitializationService(Path directory) {
        this.directories = Set.of(directory);
    }

    public DirectoryInitializationService(Set<Path> directories) {
        this.directories = directories;
    }

    protected void ensureDirectoryValid(Path directory) {
        final File filesDirectory = directory.toFile();
        if (!filesDirectory.exists()) {
            final boolean success = filesDirectory.mkdirs();
            if (!success) {
                throw new InitializationException("Failed to create directory at " + filesDirectory.getAbsolutePath());
            }
        }
        if (!filesDirectory.isDirectory()) {
            throw new InitializationException(
                    "Directory is missing or not a directory: " + filesDirectory.getAbsolutePath());
        }
        if (!Files.isWritable(filesDirectory.toPath())) {
            throw new InitializationException("Directory is not writable: " + filesDirectory.getAbsolutePath());
        }
        if (!Files.isReadable(filesDirectory.toPath())) {
            throw new InitializationException("Directory is not readable: " + filesDirectory.getAbsolutePath());
        }
    }

    @Override
    public void initialize() {
        for (Path directory : directories) {
            ensureDirectoryValid(directory);
        }
    }
}
