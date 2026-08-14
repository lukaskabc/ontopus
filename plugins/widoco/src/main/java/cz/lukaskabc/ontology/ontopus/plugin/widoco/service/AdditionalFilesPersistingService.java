package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.service.OntologyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Resolves {@link WidocoPluginConfig#relativeFilePathProperties} on the ontology and tries to match their values
 * against local files, if a file is matched, it is moved to the destination directory.
 */
@Service
public class AdditionalFilesPersistingService {
    private static final Logger log = LogManager.getLogger(AdditionalFilesPersistingService.class);

    final Set<URI> relativeFilePathProperties;
    final OntologyService ontologyService;

    public AdditionalFilesPersistingService(WidocoPluginConfig config, OntologyService ontologyService) {
        this.relativeFilePathProperties = config.getRelativeFilePathProperties();
        this.ontologyService = ontologyService;
    }

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

    public void persistAdditionalFiles(ImportProcessContext context, Path filesDestination) {
        final Path ontologyFile = context.getOntologyFilePath();
        if (ontologyFile == null) {
            log.debug("Skipping persisting additional files, no ontology file available");
            return;
        }

        final Path ontologyFileDir = ontologyFile.toFile().isFile()
                ? Objects.requireNonNull(ontologyFile.getParent(), "Ontology file does not have a parent directory")
                : ontologyFile;

        for (URI property : relativeFilePathProperties) {
            processProperty(context, property, ontologyFileDir, filesDestination);
        }
    }

    protected void processProperty(
            ImportProcessContext context, URI property, Path ontologyFileDir, Path filesDestination) {
        final Set<Path> resolvedPaths = resolveProperty(context, property);
        for (Path path : resolvedPaths) {
            final Path safeSource = FileUtils.resolvePath(context.getTempFolder(), ontologyFileDir, path);
            copyFile(safeSource, filesDestination.resolve(path));
        }
    }

    protected Set<Path> resolveProperty(ImportProcessContext context, URI property) {
        return ontologyService
                .findValue(
                        context.getTemporaryDatabaseContext(),
                        context.getVersionSeries().getOntologyURI(),
                        new ResourceURI(property))
                .stream()
                .map(str -> {
                    try {
                        return Path.of(str);
                    } catch (Exception e) {
                        log.debug("Skipping ontology property <{}>, invalid path: '{}'", property, str);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
