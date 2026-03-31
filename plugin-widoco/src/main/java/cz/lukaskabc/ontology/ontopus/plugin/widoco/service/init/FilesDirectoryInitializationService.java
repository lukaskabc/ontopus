package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.core.InitializationService;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;

/** Ensures that {@link WidocoPluginConfig#filesDirectory} exists and is writable and readable directory */
@Component
public class FilesDirectoryInitializationService implements InitializationService {
    private final WidocoPluginConfig config;

    public FilesDirectoryInitializationService(WidocoPluginConfig config) {
        this.config = config;
    }

    @Override
    public void initialize() {
        final File filesDirectory = config.getFilesDirectory();
        if (!filesDirectory.exists()) {
            final boolean success = filesDirectory.mkdirs();
            if (!success) {
                throw new IllegalStateException(
                        "Failed to create files directory at " + filesDirectory.getAbsolutePath());
            }
        }
        if (!filesDirectory.isDirectory()) {
            throw new IllegalStateException(
                    "Files directory is missing or not a directory: " + filesDirectory.getAbsolutePath());
        }
        if (!Files.isWritable(filesDirectory.toPath())) {
            throw new IllegalStateException("Files directory is not writable: " + filesDirectory.getAbsolutePath());
        }
        if (!Files.isReadable(filesDirectory.toPath())) {
            throw new IllegalStateException("Files directory is not readable: " + filesDirectory.getAbsolutePath());
        }
    }
}
