package cz.lukaskabc.ontology.ontopus.core.service.resource_fallback;

import cz.lukaskabc.ontology.ontopus.api.rest.StreamingResponseBody;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class TrailingSlashFallbackService extends ResourceRequestFallbackService {
    private final OntopusConfig.Resource resourceConfig;

    public TrailingSlashFallbackService(ResourceRequestFallbackService fallbackService, OntopusConfig ontopusConfig) {
        super(fallbackService);
        this.resourceConfig = ontopusConfig.getResource();
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getResourceWithFallback(
            ResourceURI resourceURI, MediaType[] mediaTypes) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(resourceURI.toURI());
        UriComponents components = builder.build();
        final String path = components.getPath();
        if (path == null) {
            return super.getResourceWithFallback(resourceURI, mediaTypes);
        }

        if (resourceConfig.isTrailingSlashFallsBackToNoSlash() && path.endsWith("/")) {
            final String withoutSlash = StringUtils.withoutTrailingSlash(path);
            builder.replacePath(withoutSlash);
        } else if (resourceConfig.isNoSlashFallsBackToTrailingSlash() && !path.endsWith("/")) {
            builder.replacePath(path + "/");
        }
        return super.getResourceWithFallback(new ResourceURI(builder.build().toUri()), mediaTypes);
    }
}
