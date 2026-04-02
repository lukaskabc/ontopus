package cz.lukaskabc.ontology.ontopus.plugin.widoco.rest;

import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.stream.Stream;

@RequestMapping(WidocoController.PATH)
@RestController
public class WidocoController {
    static final String PATH = "/public/widoco";
    public static final String ONTOLOGY_QUERY_PARAM = "ontology";
    private static final Logger log = LogManager.getLogger(WidocoController.class);
    static final PathMatcher DEFAULT_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**index*.html");

    private static String resolveRequestedPath(HttpServletRequest request) {
        final String fullPath = request.getRequestURI();
        if (fullPath.startsWith(PATH)) {
            return fullPath.substring(PATH.length());
        }
        throw new NotFoundException("Invalid path requested");
    }

    private final Path filesDirectory;

    public WidocoController(WidocoPluginConfig pluginConfig) {
        this.filesDirectory = pluginConfig.getFilesDirectory();
    }

    FileSystemResource getFile(Path artifactDirectory, Path requestedPath) {
        final Path absoluteDirPath = requestedPath.toString().isEmpty()
                ? artifactDirectory
                : FileUtils.resolvePath(filesDirectory, artifactDirectory);
        final Path absolutePath = FileUtils.resolvePath(absoluteDirPath, requestedPath);
        if (Files.isRegularFile(absolutePath)) {
            return new FileSystemResource(absolutePath);
        } else if (Files.isDirectory(absolutePath)) {
            return resolveDefaultFile(absolutePath);
        } else {
            throw new NotFoundException("Requested resource not found");
        }
    }

    @GetMapping("**")
    public FileSystemResource getOntologyFile(
            @RequestParam(ONTOLOGY_QUERY_PARAM) VersionArtifactURI artifactURI, HttpServletRequest request) {
        final Path directoryName = Path.of(StringUtils.sanitize(artifactURI.toString()));
        final Path directory = FileUtils.resolvePath(filesDirectory, directoryName);
        if (!Files.isDirectory(directory)) {
            log.trace("Widoco output directory not found for {}", artifactURI);
            throw new NotFoundException("Widoco output directory not found for the requested ontology");
        }

        final String requestedPath = resolveRequestedPath(request);
        return getFile(directory, Path.of(requestedPath));
    }

    FileSystemResource resolveDefaultFile(Path safeDirectoryPath) {
        // TODO handle requested language?
        try (Stream<Path> stream = Files.list(safeDirectoryPath)) {
            return stream.filter(DEFAULT_FILE_MATCHER::matches)
                    .findAny()
                    .map(FileSystemResource::new)
                    .orElseThrow(() -> new NotFoundException("No default file found in the requested directory"));
        } catch (Exception e) {
            throw new NotFoundException("Error while resolving default file in the requested directory", e);
        }
    }
}
