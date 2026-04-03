package cz.lukaskabc.ontology.ontopus.api.service.core;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

@NullMarked
public interface MediaTypeResolver {
    /** Map an extension to a MediaType. Ignore if extension already mapped. */
    void addMapping(String extension, MediaType mediaType);

    /**
     * Resolve the given media type to a list of file extensions.
     *
     * @param mediaType the media type to resolve
     * @return a list of extensions or an empty list (never {@code null})
     * @see org.springframework.web.accept.MediaTypeFileExtensionResolver
     */
    List<String> resolveFileExtensions(MediaType mediaType);

    /**
     * Resolve the given file extension to a media type.
     *
     * @param fileExtension the file extension to resolve
     * @return the media type if resolved
     */
    Optional<MediaType> resolveMediaType(String fileExtension);
}
