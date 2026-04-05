package cz.lukaskabc.ontology.ontopus.plugin.rdf.config;

import cz.lukaskabc.ontology.ontopus.api.service.core.MediaTypeResolver;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@Configuration
public class MediaTypeConfiguration {
    private final MediaTypeResolver mediaTypeResolver;

    public MediaTypeConfiguration(MediaTypeResolver mediaTypeResolver) {
        this.mediaTypeResolver = mediaTypeResolver;
    }

    @PostConstruct
    void registerMediaTypes() {
        RDFWriterRegistry.getInstance().getKeys().stream()
                .map(rdfFormat -> Map.entry(rdfFormat.getFileExtensions(), rdfFormat.getMIMETypes()))
                .forEach(entry -> {
                    for (String extension : entry.getKey()) {
                        for (String mimeType : entry.getValue()) {
                            mediaTypeResolver.addMapping(extension, MediaType.parseMediaType(mimeType));
                        }
                    }
                });
        RDFFormat.JSONLD
                .getMIMETypes()
                .forEach(mimeType -> mediaTypeResolver.addMapping("jsonld", MediaType.parseMediaType(mimeType)));
    }
}
