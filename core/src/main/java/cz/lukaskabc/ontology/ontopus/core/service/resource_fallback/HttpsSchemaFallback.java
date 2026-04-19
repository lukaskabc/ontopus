package cz.lukaskabc.ontology.ontopus.core.service.resource_fallback;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Exchanges {@code HTTP} and {@code HTTPS} schemes based on the requested scheme and the configuration
 * {@link OntopusConfig#getResource()}
 */
@Component
public class HttpsSchemaFallback implements ResourceRequestFallback {
    private final OntopusConfig.Resource resourceConfig;

    public HttpsSchemaFallback(OntopusConfig ontopusConfig) {
        this.resourceConfig = ontopusConfig.getResource();
    }

    @Override
    public void accept(UriComponentsBuilder builder) {
        UriComponents components = builder.build();
        if (resourceConfig.isHttpFallsBackToHttps() && "http".equals(components.getScheme())) {
            builder.scheme("https");
        } else if (resourceConfig.isHttpsFallsBackToHttp() && "https".equals(components.getScheme())) {
            builder.scheme("http");
        }
    }
}
