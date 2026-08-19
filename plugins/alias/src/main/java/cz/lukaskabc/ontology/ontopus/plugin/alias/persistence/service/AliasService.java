package cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service;

import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.repository.URIAliasMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AliasService {
    private final Map<URI, URI> temporaryAliases = new HashMap<>();
    private final URIAliasMappingRepository repository;

    public AliasService(URIAliasMappingRepository repository) {
        this.repository = repository;
    }

    public URIAliasMapping createMapping(URI resource, URI alias) {
        final URIAliasMapping mapping = new URIAliasMapping(normalize(resource), normalize(alias));
        repository.save(mapping);
        return mapping;
    }

    public void createTemporaryMapping(URI resource, URI alias) {
        temporaryAliases.put(normalize(resource), normalize(alias));
    }

    public Optional<URI> findAliasFor(URI uri) {
        final URI normalized = normalize(uri);
        if (temporaryAliases.containsKey(normalized)) {
            return Optional.of(temporaryAliases.get(normalized));
        }
        return repository.findAliasFor(normalized);
    }

    /**
     * Replaces the {@code HTTP} scheme with {@code HTTPS} and strips trailing slash.
     *
     * @param uri the URI to normalize
     * @return normalized URI
     */
    protected URI normalize(URI uri) {
        Objects.requireNonNull(uri, "URI cannot be null");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
        UriComponents comp = builder.build(true);

        if ("https".equalsIgnoreCase(comp.getScheme())) {
            builder.scheme("http");
        }

        builder.replacePath(StringUtils.withoutTrailingSlash(comp.getPath()));
        return builder.build(true).toUri();
    }
}
