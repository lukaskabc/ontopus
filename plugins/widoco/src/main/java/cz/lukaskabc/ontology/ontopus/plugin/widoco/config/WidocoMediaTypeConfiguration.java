package cz.lukaskabc.ontology.ontopus.plugin.widoco.config;

import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import jakarta.annotation.PostConstruct;

@Configuration
public class WidocoMediaTypeConfiguration {
    private final MediaTypeResolver mediaTypeResolver;

    public WidocoMediaTypeConfiguration(MediaTypeResolver mediaTypeResolver) {
        this.mediaTypeResolver = mediaTypeResolver;
    }

    @PostConstruct
    void registerMediaTypes() {
        mediaTypeResolver.addMapping("html", MediaType.TEXT_HTML);
    }
}
