package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Resolves {@link WidocoPluginConfig#relativeFilePathProperties} on the ontology and tries to match their values
 * against local files, if a file is matched, it is moved to the destination directory.
 */
@Service
public class AdditionalFilesPersistingService {
    private static final Logger log = LogManager.getLogger(AdditionalFilesPersistingService.class);
    public static final Object FILE_PATHS_ADDITIONAL_PROPERTY = new Object();

    protected void copyFile(Path source, Path destinationDirectory) {
        log.trace("Moving additional file from {} to {}", source, destinationDirectory);
        try {
            Files.createDirectories(destinationDirectory);
            Files.copy(source, destinationDirectory, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw log.throwing(InternalException.builder()
                    .errorType(Vocabulary.u_i_ontopus_problem_file_processing)
                    .internalMessage("Failed to copy additional Widoco file")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .titleMessageCode("ontopus.plugin.widoco.error.copyAdditionalFile")
                    .cause(e)
                    .build());
        }
    }

    @Transactional
    public void persistAdditionalFiles(ImportProcessContext context, Path filesDestination) {
        @SuppressWarnings("unchecked")
        final List<Path> relativePaths = context.getAdditionalProperty(FILE_PATHS_ADDITIONAL_PROPERTY, List.class)
                .orElseThrow();

        for (Path relativeDestination : relativePaths) {
            final Path safeSource = FileUtils.resolvePath(context.getTempFolder(), relativeDestination);
            final Path absoluteDestination = filesDestination.resolve(relativeDestination);
            copyFile(safeSource, absoluteDestination);
        }
    }
}
