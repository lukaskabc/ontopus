package cz.lukaskabc.ontology.ontopus.core.service.resource_fallback;

import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public abstract class ResourceRequestFallbackService {
    @Nullable private final ResourceRequestFallbackService fallbackService;

    protected ResourceRequestFallbackService(@Nullable ResourceRequestFallbackService fallbackService) {
        this.fallbackService = fallbackService;
    }

    public ResponseEntity<StreamingResponseBody> getResource(ResourceURI resourceURI, MediaType[] mediaTypes) {
        if (fallbackService == null) {
            return getResourceWithFallback(resourceURI, mediaTypes);
        }
        final ResponseEntity<StreamingResponseBody> result = fallbackService.getResource(resourceURI, mediaTypes);
        if (result.getStatusCode().is4xxClientError()) {
            return getResourceWithFallback(resourceURI, mediaTypes);
        }
        return result;
    }

    protected ResponseEntity<StreamingResponseBody> getResourceWithFallback(
            ResourceURI resourceURI, MediaType[] mediaTypes) {
        if (fallbackService != null) {
            return fallbackService.getResource(resourceURI, mediaTypes);
        }
        return ResponseEntity.notFound().build();
    }
}
