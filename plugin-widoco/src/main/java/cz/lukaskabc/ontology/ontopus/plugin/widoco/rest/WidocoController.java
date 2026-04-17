package cz.lukaskabc.ontology.ontopus.plugin.widoco.rest;

import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.exception.NotFoundException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.config.WidocoPluginConfig;
import org.apache.commons.lang3.stream.Streams;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * Resolves the requested path inside the version artifact directory
     *
     * @param base64EncodedUri the base64 encoded IRI
     * @param request the HTTP request
     * @return the requested decoded path
     */
    private static String resolveRequestedPath(String base64EncodedUri, HttpServletRequest request) {
        final String path = UriUtils.decode(request.getRequestURI(), "UTF-8");
        if (path.contains(base64EncodedUri)) {
            return path.substring(path.indexOf(base64EncodedUri) + base64EncodedUri.length());
        }
        throw NotFoundException.builder()
                .internalMessage("Requested path does not contain the original base64 encoded URI")
                .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                .build();
    }

    private final Path filesDirectory;
    private final URI systemURI;

    public WidocoController(WidocoPluginConfig pluginConfig, OntopusConfig ontopusConfig) {
        this.filesDirectory = pluginConfig.getFilesDirectory();
        this.systemURI = ontopusConfig.getSystemUri();
    }

    @GetMapping("/{base64EncodedUri}/**")
    public ResponseEntity<FileSystemResource> getOntologyFile(
            @PathVariable("base64EncodedUri") String base64EncodedUri, HttpServletRequest request) throws IOException {
        // sanitized version IRI of the artifact
        final String decodedSanitizedUri =
                StringUtils.sanitizeUriAsComponent(StringUtils.base64DecodeUri(base64EncodedUri));
        // the requested path inside the version artifact directory
        final String requestedPath = resolveRequestedPath(base64EncodedUri, request);
        // relative requested path inside the widoco directory
        final Path relativeRequestedPath = ROOT_PATH.relativize(Path.of("/" + decodedSanitizedUri, requestedPath));
        final Path absolutePath = FileUtils.resolvePath(filesDirectory, relativeRequestedPath);

        if (Files.isRegularFile(absolutePath)) {
            return serveFile(absolutePath);
        } else if (Files.isDirectory(absolutePath)) {
            final Path index = resolveDefaultFileName(absolutePath, request);
            return redirectTo(base64EncodedUri, requestedPath, index.toString());
        } else {
            throw NotFoundException.builder()
                    .internalMessage("Requested Widoco resource not found")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }
    }

    ResponseEntity<FileSystemResource> redirectTo(String base64EncodedUri, String requestedPath, String indexFile) {
        final String destination = UriComponentsBuilder.fromUri(systemURI)
                .path(PATH)
                .path("/")
                .path(base64EncodedUri)
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
                throw NotFoundException.builder()
                        .internalMessage("No default file found in the requested directory")
                        .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                        .build();
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
            throw NotFoundException.builder()
                    .internalMessage("Error while resolving default file in the requested directory")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .cause(e)
                    .build();
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
