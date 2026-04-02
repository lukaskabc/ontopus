package cz.lukaskabc.ontology.ontopus.plugin.widoco.rest;

import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RequestMapping(WidocoController.PATH)
@RestController
public class WidocoController {
    static final String PATH = "/public/widoco";
    public static final String ONTOLOGY_QUERY_PARAM = "ontology";
    private static final Logger log = LogManager.getLogger(WidocoController.class);
    static final PathMatcher DEFAULT_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**index*.html");

    private static String resolveRequestedPath(HttpServletRequest request) {
        final String path = UriUtils.decode(request.getRequestURI(), "UTF-8");
        if (path.startsWith(PATH)) {
            final String subpath = path.substring(PATH.length());
            if (subpath.startsWith("/")) {
                return "." + subpath;
            }
            return subpath;
        }
        throw new NotFoundException("Invalid path requested");
    }

    private final Path filesDirectory;

    public WidocoController(WidocoPluginConfig pluginConfig) {
        this.filesDirectory = pluginConfig.getFilesDirectory();
    }

    ResponseEntity<FileSystemResource> getFile(Path requestedPath) {
        final Path absolutePath = FileUtils.resolvePath(filesDirectory, requestedPath);
        if (Files.isRegularFile(absolutePath)) {
            return ResponseEntity.ok(new FileSystemResource(absolutePath));
        } else if (Files.isDirectory(absolutePath)) {
            return resolveDefaultFile(absolutePath);
        } else {
            throw new NotFoundException("Requested resource not found");
        }
    }



    @GetMapping("/**")
    public ResponseEntity<FileSystemResource> getOntologyFile(HttpServletRequest request) {
        final String requestedPath = resolveRequestedPath(request);
        return getFile(Path.of(requestedPath));
    }

    ResponseEntity<FileSystemResource> resolveDefaultFile(Path safeDirectoryPath) {
        // TODO handle requested language?
        try (Stream<Path> stream = Files.list(safeDirectoryPath)) {
            final Path index = stream.filter(DEFAULT_FILE_MATCHER::matches)
                    .findAny()
                    .orElseThrow(() -> new NotFoundException("No default file found in the requested directory"));
            // TODO: redirect to absolute path
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "./" + index.getFileName().toString())
                    .build();
        } catch (Exception e) {
            throw new NotFoundException("Error while resolving default file in the requested directory", e);
        }
    }
}
