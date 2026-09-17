package cz.lukaskabc.ontology.ontopus.core_model.util;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UriUtils {
    /** @see #withoutSuffix(URI) */
    public static ResourceURI withoutSuffix(ResourceURI original) {
        return new ResourceURI(withoutSuffix(original.toURI()));
    }

    /**
     * Removes the file extension from the URI.
     *
     * @param original the URI from which the extension should be removed
     * @return URI with file extension removed
     */
    public static URI withoutSuffix(URI original) {
        final String fileExt = org.springframework.util.StringUtils.getFilenameExtension(original.getPath());
        if (fileExt == null) {
            return original;
        }

        String originalPath = original.getPath();
        String newPath = originalPath.substring(0, originalPath.length() - fileExt.length() - 1);

        return UriComponentsBuilder.fromUri(original)
                .replacePath(newPath)
                .build()
                .toUri();
    }

    /**
     * Removes trailing slashes from the URI
     *
     * @param uri the uri to trim
     * @return a new uri without trailing slashes or the {@code uri} object if there is no trailing slash
     */
    @SuppressWarnings({"StringEquality", "ReferenceEquality"})
    public static URI withoutTrailingSlash(URI uri) {
        final String str = uri.toString();
        final String withoutTrailingSlash = StringUtils.withoutTrailingSlash(str);
        // withoutTrailingSlash guarantees to return the param1 if no change is required
        if (str == withoutTrailingSlash) {
            return uri;
        }
        try {
            return new URI(withoutTrailingSlash);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI: " + uri, e);
        }
    }

    private UriUtils() {
        throw new AssertionError();
    }
}
