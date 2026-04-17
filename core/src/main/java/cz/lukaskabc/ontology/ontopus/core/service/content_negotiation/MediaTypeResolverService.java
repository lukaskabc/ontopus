package cz.lukaskabc.ontology.ontopus.core.service.content_negotiation;

import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InitializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.MappingMediaTypeFileExtensionResolver;
import org.springframework.web.accept.MediaTypeFileExtensionResolver;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@NullMarked
@Service
public class MediaTypeResolverService extends MappingMediaTypeFileExtensionResolver implements MediaTypeResolver {
    private static final Logger log = LogManager.getLogger(MediaTypeResolverService.class);

    @SuppressWarnings("unchecked")
    private static MultiValueMap<String, MediaType> extractMapFromFactory() {
        try {
            Field field = MediaTypeFactory.class.getDeclaredField("fileExtensionToMediaTypes");
            field.setAccessible(true);
            return (MultiValueMap<String, MediaType>) field.get(null);
        } catch (Exception e) {
            throw log.throwing(new InitializationException("Failed to extract media type map from Spring", e));
        }
    }

    private static Optional<MediaType> resolveMediaTypeFromFactory(String fileExtension) {
        return MediaTypeFactory.getMediaType("file." + fileExtension);
    }

    /**
     * Merges Media Type to file extensions maps from the {@link ContentNegotiationManager} and
     * {@link MediaTypeFactory}. More specific media types gets priority.
     */
    private static Map<String, MediaType> resolveMediaTypes(ContentNegotiationManager manager) {
        final MultiValueMap<String, MediaType> springInternalMap = extractMapFromFactory();
        final Map<String, MediaType> managerMap = manager.getMediaTypeMappings();
        HashMap<String, MediaType> result = new HashMap<>(springInternalMap.size() + managerMap.size());
        BiConsumer<String, MediaType> addAction = (extension, type) -> {
            result.compute(extension, (_, existing) -> {
                if (existing == null || type.isMoreSpecific(existing)) {
                    return type;
                }
                return existing;
            });
        };
        managerMap.forEach(addAction);
        springInternalMap.forEach((ext, types) -> types.forEach(type -> addAction.accept(ext, type)));
        return result;
    }

    public MediaTypeResolverService(ContentNegotiationManager manager) {
        super(resolveMediaTypes(manager));
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
