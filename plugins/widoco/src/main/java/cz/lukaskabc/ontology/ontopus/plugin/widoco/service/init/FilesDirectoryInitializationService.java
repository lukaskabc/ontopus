package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.init;

import cz.lukaskabc.ontology.ontopus.api.service.init.DirectoryInitializationService;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Ensures that {@link WidocoPluginConfig#filesDirectory} exists and is writable and readable directory */
@Order(DirectoryInitializationService.ORDER)
@Component
public class FilesDirectoryInitializationService extends DirectoryInitializationService {
    public FilesDirectoryInitializationService(WidocoPluginConfig config) {
        super(config.getFilesDirectory());
    }
}
