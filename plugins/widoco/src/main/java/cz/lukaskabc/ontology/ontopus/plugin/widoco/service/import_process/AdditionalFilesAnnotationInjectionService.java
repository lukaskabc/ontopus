package cz.lukaskabc.ontology.ontopus.plugin.widoco.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyAnnotationInjectionService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.service.OntologyService;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.service.AdditionalFilesPersistingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.Model;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Non-interactive service resolving common widoco properties with relative file paths and replacing them with paths
 * relative to the widoco publication root.
 *
 * @see AdditionalFilesPersistingService
 */
@Service
public class AdditionalFilesAnnotationInjectionService implements OntologyAnnotationInjectionService {
    private static final Logger log = LogManager.getLogger(AdditionalFilesAnnotationInjectionService.class);
    private final Set<URI> relativeFilePathProperties;
    private final OntologyService ontologyService;

    public AdditionalFilesAnnotationInjectionService(WidocoPluginConfig config, OntologyService ontologyService) {
        this.relativeFilePathProperties = config.getRelativeFilePathProperties();
        this.ontologyService = ontologyService;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public Model handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        final Path ontologyFile = context.getOntologyFilePath();
        if (ontologyFile == null) {
            log.debug("Skipping persisting additional files, no ontology file available");
            return EMPTY_MODEL;
        }

        final Path ontologyFileDir = ontologyFile.toFile().isFile()
                ? Objects.requireNonNull(ontologyFile.getParent(), "Ontology file does not have a parent directory")
                : ontologyFile;

        context.setAdditionalProperty(
                AdditionalFilesPersistingService.FILE_PATHS_ADDITIONAL_PROPERTY,
                new ArrayList<Path>(relativeFilePathProperties.size()));

        for (URI property : relativeFilePathProperties) {
            processProperty(context, property, ontologyFileDir);
        }

        return EMPTY_MODEL;
    }

    protected void processProperty(ImportProcessContext context, URI property, Path ontologyFileDir) {
        final Set<Path> resolvedPaths = resolveProperty(context, property);

        @SuppressWarnings("unchecked")
        final List<Path> relativePaths = context.getAdditionalProperty(
                        AdditionalFilesPersistingService.FILE_PATHS_ADDITIONAL_PROPERTY, List.class)
                .orElseThrow();

        for (Path path : resolvedPaths) {
            final Path safeSource = FileUtils.resolvePath(context.getTempFolder(), ontologyFileDir, path);
            final Path relativeDestination = resolveDestinationRelativePath(context.getTempFolder(), safeSource);
            log.debug(
                    "Replacing relative file path in property <{}> from '{}' to '{}'",
                    property,
                    path,
                    relativeDestination);

            ontologyService.replaceObjectStringValue(
                    context.getTemporaryDatabaseContext(),
                    context.getVersionSeries().getOntologyURI(),
                    new ResourceURI(property),
                    path.toString(),
                    relativeDestination.toString());
            relativePaths.add(relativeDestination);
        }
    }

    protected Path resolveDestinationRelativePath(Path absoluteRoot, Path source) {
        String absoluteRootStr = absoluteRoot.toString();
        String sourceStr = source.toString();
        int commonPrefix = 0;
        for (int i = 0; i < Math.min(absoluteRootStr.length(), sourceStr.length()); i++) {
            if (absoluteRootStr.charAt(i) == sourceStr.charAt(i)) {
                commonPrefix++;
            } else {
                break;
            }
        }
        return FileUtils.forceRelativePath(sourceStr.substring(commonPrefix));
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
