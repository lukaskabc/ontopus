package cz.lukaskabc.ontology.ontopus.core.service.content_negotiation;

import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.MappingMediaTypeFileExtensionResolver;
import org.springframework.web.accept.MediaTypeFileExtensionResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@NullMarked
@Service
public class MediaTypeResolverService extends MappingMediaTypeFileExtensionResolver implements MediaTypeResolver {

    private static Optional<MediaType> resolveMediaTypeFromFactory(String fileExtension) {
        return MediaTypeFactory.getMediaType("file." + fileExtension);
    }

    public MediaTypeResolverService(ContentNegotiationManager manager) {
        super(new HashMap<>(manager.getMediaTypeMappings()));
    }

    /** Map an extension to a MediaType. Ignore if extension already mapped. */
    @Override
    public void addMapping(String extension, MediaType mediaType) {
        super.addMapping(extension, mediaType);
    }

    /**
     * Resolve the given media type to a list of file extensions.
     *
     * @param mediaType the media type to resolve
     * @return a list of extensions or an empty list (never {@code null})
     * @see MediaTypeFileExtensionResolver
     */
    @Override
    public List<String> resolveFileExtensions(MediaType mediaType) {
        return super.resolveFileExtensions(mediaType);
    }

    /**
     * Resolve the given file extension to a media type.
     *
     * @param fileExtension the file extension to resolve
     * @return the media type if resolved
     */
    @Override
    public Optional<MediaType> resolveMediaType(String fileExtension) {
        return Optional.ofNullable(super.lookupMediaType(fileExtension))
                .or(() -> resolveMediaTypeFromFactory(fileExtension));
    }
}
