package cz.lukaskabc.ontology.ontopus.plugin.widoco.rest;

import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.commons.lang3.stream.Streams;
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
import java.util.List;
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
            final String base64EncodedUrl = path.substring(PATH.length());
            final String decodedUrl = StringUtils.base64DecodeUri(base64EncodedUrl);
            return StringUtils.sanitizeUriAsComponent(decodedUrl);
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

        if (Files.isRegularFile(absolutePath)) {
            return serveFile(absolutePath);
        } else if (Files.isDirectory(absolutePath)) {
            final Path index = resolveDefaultFileName(absolutePath, request);
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

    Path resolveDefaultFileName(Path safeDirectory, HttpServletRequest request) {
        final Stream<String> languages = Streams.of(request.getLocales())
                .flatMap(locale -> Stream.of(locale.getCountry(), locale.getLanguage()))
                .distinct();

        try (Stream<Path> stream = Files.list(safeDirectory)) {
            final List<Path> indexFiles =
                    stream.filter(DEFAULT_FILE_MATCHER::matches).toList();
            if (indexFiles.isEmpty()) {
                throw new NotFoundException("No default file found in the requested directory");
            }
            final Path index = languages
                    // map languages to index files (preserving order of languages)
                    .flatMap(lang ->
                            indexFiles.stream()
                                    .filter(path -> path.endsWith("index-" + lang + ".html"))
                                    .findFirst()
                                    .stream())
                    .findFirst()
                    .orElseGet(indexFiles::getFirst);
            return index.getFileName();
        } catch (IOException e) {
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
