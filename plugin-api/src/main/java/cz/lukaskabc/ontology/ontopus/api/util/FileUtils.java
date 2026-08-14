package cz.lukaskabc.ontology.ontopus.api.util;

import com.google.errorprone.annotations.MustBeClosed;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class FileUtils {
    private static final Logger log = LogManager.getLogger(FileUtils.class);

    /** Strips any root component, forcing the path to be relative. */
    public static Path forceRelativePath(String pathString) {
        Objects.requireNonNull(pathString, "pathString must not be null");
        Path path = Path.of(pathString);

        if (path.isAbsolute() || path.getRoot() != null) {
            Path root = path.getRoot();
            return root != null ? root.relativize(path) : path;
        }

        return path;
    }

    @MustBeClosed
    public static Stream<Path> listRecursively(Path directory) {
        Objects.requireNonNull(directory, "directory must not be null");
        try {
            return Files.walk(directory);
        } catch (IOException e) {
            throw log.throwing(InternalException.fileProcessingException("Failure during directory iteration", e));
        }
    }

    /**
     * Resolves an untrusted user-specified path against the API's base directory. Paths that try to escape the base
     * directory are rejected.
     *
     * @param baseDirPath the absolute path of the base directory that all user-specified paths should be within
     * @param userPath the untrusted path provided by the API user, expected to be relative to {@code baseDirPath}
     * @see <a href="https://stackoverflow.com/a/33084369/12690791">Author at StackOverflow</a>
     * @throws ValidationException if the resolved path escapes the base directory
     */
    public static Path resolvePath(final Path baseDirPath, final Path userPath) {
        return resolvePath(baseDirPath, baseDirPath, userPath);
    }

    /**
     * Resolves an untrusted user-specified path against the API's base directory. Paths that try to escape the absolute
     * root directory are rejected.
     *
     * @param absoluteRoot the absolute path of the root directory that all user-specified paths should be within
     * @param baseDirPath the absolute path against which the user path should be resolved
     * @param userPath the untrusted path provided by the API user, expected to be relative to {@code baseDirPath} and
     *     must not escape {@code absoluteRoot}
     * @see <a href="https://stackoverflow.com/a/33084369/12690791">Author at StackOverflow</a>
     * @throws ValidationException if the resolved path escapes the absolute root directory
     */
    public static Path resolvePath(final Path absoluteRoot, final Path baseDirPath, final Path userPath) {
        Objects.requireNonNull(absoluteRoot, "absoluteRoot must not be null");
        Objects.requireNonNull(baseDirPath, "baseDirPath must not be null");
        Objects.requireNonNull(userPath, "userPath must not be null");

        if (!absoluteRoot.isAbsolute()) {
            throw ValidationException.builder()
                    .internalMessage("Root path must be absolute")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        if (!baseDirPath.isAbsolute()) {
            throw ValidationException.builder()
                    .internalMessage("Base path must be absolute")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        if (userPath.isAbsolute()) {
            throw ValidationException.builder()
                    .internalMessage("User path must be relative")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        // Normalize roots and base paths to ensure consistent segment comparisons
        final Path normalizedRoot = absoluteRoot.normalize();
        final Path normalizedBase = baseDirPath.normalize();

        if (!normalizedBase.startsWith(normalizedRoot)) {
            throw ValidationException.builder()
                    .internalMessage("Base directory is outside of the absolute root directory")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        // Join the two paths together, then normalize so that any ".." elements
        // in the userPath can remove parts of baseDirPath.
        // (e.g. "/foo/bar/baz" + "../attack" -> "/foo/bar/attack")
        final Path resolvedPath = normalizedBase.resolve(userPath).normalize();

        // Make sure the resulting path is still within the required directory.
        // (In the example above, "/foo/bar/attack" is not.)
        if (!resolvedPath.startsWith(normalizedRoot)) {
            throw ValidationException.builder()
                    .internalMessage("User path escapes the absolute root directory")
                    .detailMessageArguments(OntopusException.EMPTY_ARGUMENTS)
                    .build();
        }

        return resolvedPath;
    }

    private FileUtils() {
        throw new AssertionError();
    }
}
