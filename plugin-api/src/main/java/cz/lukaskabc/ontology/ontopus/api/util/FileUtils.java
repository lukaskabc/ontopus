package cz.lukaskabc.ontology.ontopus.api.util;

import java.nio.file.Path;

public class FileUtils {
    /**
     * Resolves an untrusted user-specified path against the API's base directory. Paths that try to escape the base
     * directory are rejected.
     *
     * @param baseDirPath the absolute path of the base directory that all user-specified paths should be within
     * @param userPath the untrusted path provided by the API user, expected to be relative to {@code baseDirPath}
     * @see <a href="https://stackoverflow.com/a/33084369/12690791">Author at StackOverflow</a>
     */
    public static Path resolvePath(final Path baseDirPath, final Path userPath) {
        if (!baseDirPath.isAbsolute()) {
            throw new IllegalArgumentException("Base path must be absolute");
        }

        if (userPath.isAbsolute()) {
            throw new IllegalArgumentException("User path must be relative");
        }

        // Join the two paths together, then normalize so that any ".." elements
        // in the userPath can remove parts of baseDirPath.
        // (e.g. "/foo/bar/baz" + "../attack" -> "/foo/bar/attack")
        final Path resolvedPath = baseDirPath.resolve(userPath).normalize();

        // Make sure the resulting path is still within the required directory.
        // (In the example above, "/foo/bar/attack" is not.)
        if (!resolvedPath.startsWith(baseDirPath)) {
            throw new IllegalArgumentException("User path escapes the base path");
        }

        return resolvedPath;
    }

    private FileUtils() {
        throw new AssertionError();
    }
}
