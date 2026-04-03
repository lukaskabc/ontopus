package cz.lukaskabc.ontology.ontopus.plugin.widoco.rest;

import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.stream.Stream;

@RequestMapping(WidocoController.PATH)
@RestController
public class WidocoController {
    private static final Path ROOT_PATH = Path.of("/");
    static final String PATH = "/public/widoco";
    static final PathMatcher DEFAULT_FILE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**index*.html");

    private static String resolveRequestedPath(HttpServletRequest request) {
        final String path = UriUtils.decode(request.getRequestURI(), "UTF-8");
        if (path.startsWith(PATH)) {
            return path.substring(PATH.length());
        }
        throw new NotFoundException("Invalid path requested");
    }

    private final Path filesDirectory;
    private final URI systemURI;

    public WidocoController(WidocoPluginConfig pluginConfig, OntopusConfig ontopusConfig) {
        this.filesDirectory = pluginConfig.getFilesDirectory();
        this.systemURI = ontopusConfig.getSystemUri();
    }

    @GetMapping("/**")
    public ResponseEntity<FileSystemResource> getOntologyFile(HttpServletRequest request) throws IOException {
        final String requestedPath = resolveRequestedPath(request);
        final Path relativeRequestedPath = ROOT_PATH.relativize(Path.of(requestedPath));
        final Path absolutePath = FileUtils.resolvePath(filesDirectory, relativeRequestedPath);
        // TODO: handle requested language?

        if (Files.isRegularFile(absolutePath)) {
            return serveFile(absolutePath);
        } else if (Files.isDirectory(absolutePath)) {
            final Path index = resolveDefaultFileName(absolutePath);
            return redirectTo(requestedPath, index.toString());
        } else {
            throw new NotFoundException("Requested resource not found");
        }
    }

    ResponseEntity<FileSystemResource> redirectTo(String requestedPath, String indexFile) {
        final String destination = UriComponentsBuilder.fromUri(systemURI)
                .path(PATH)
                .path("/")
                .path(requestedPath)
                .path("/")
                .path(indexFile)
                .toUriString();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", destination)
                .build();
    }

    Path resolveDefaultFileName(Path safeDirectory) {
        try (Stream<Path> stream = Files.list(safeDirectory)) {
            final Path index = stream.filter(DEFAULT_FILE_MATCHER::matches)
                    .findAny()
                    .orElseThrow(() -> new NotFoundException("No default file found in the requested directory"));
            // TODO: redirect to absolute path
            return index.getFileName();
        } catch (Exception e) {
            throw new NotFoundException("Error while resolving default file in the requested directory", e);
        }
    }

    ResponseEntity<FileSystemResource> serveFile(Path file) throws IOException {
        final FileSystemResource fileResource = new FileSystemResource(file);
        final MediaType mediaType =
                MediaTypeFactory.getMediaType(fileResource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .lastModified(fileResource.lastModified())
                .body(fileResource);
    }
}
