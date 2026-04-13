package cz.lukaskabc.ontology.ontopus.core.service.resource_fallback;

import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class HttpsSchemaFallbackService extends ResourceRequestFallbackService {
    private final OntopusConfig.Resource resourceConfig;

    public HttpsSchemaFallbackService(
            @Nullable ResourceRequestFallbackService fallbackService, OntopusConfig ontopusConfig) {
        super(fallbackService);
        this.resourceConfig = ontopusConfig.getResource();
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getResourceWithFallback(
            ResourceURI resourceURI, MediaType @Nullable [] mediaTypes) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(resourceURI.toURI());
        UriComponents components = builder.build();
        if (resourceConfig.isHttpFallsBackToHttps() && "http".equals(components.getScheme())) {
            URI httpsUri = builder.scheme("https").build().toUri();
            return super.getResourceWithFallback(new ResourceURI(httpsUri), mediaTypes);
        } else if (resourceConfig.isHttpsFallsBackToHttp() && "https".equals(components.getScheme())) {
            URI httpsUri = builder.scheme("http").build().toUri();
            return super.getResourceWithFallback(new ResourceURI(httpsUri), mediaTypes);
        }
        return super.getResourceWithFallback(resourceURI, mediaTypes);
    }
}
