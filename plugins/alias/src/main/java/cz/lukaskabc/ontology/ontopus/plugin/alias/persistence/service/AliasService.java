package cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service;

import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.repository.URIAliasMappingRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

@Service
public class AliasService {
    private final Map<URI, URI> staticAliases = new HashMap<>();
    private final URIAliasMappingRepository repository;

    public AliasService(URIAliasMappingRepository repository) {
        this.repository = repository;
    }

    public URIAliasMapping createMapping(URI resource, URI alias) {
        final URIAliasMapping mapping = new URIAliasMapping(resource, alias);
        repository.save(mapping);
        return mapping;
    }

    public void createStaticMapping(URI resource, URI alias) {
        staticAliases.put(URIAliasMapping.normalize(resource), URIAliasMapping.normalize(alias));
    }

    public Optional<URI> findAliasFor(URI uri) {
        final URI normalized = URIAliasMapping.normalize(uri);
        if (staticAliases.containsKey(normalized)) {
            return Optional.of(staticAliases.get(normalized));
        }
        return repository.findAliasFor(normalized);
    }

    public Stream<URIAliasMapping> findAllMappings() {
        return repository.findAll();
    }

    public Map<URI, URI> getStaticAliases() {
        return Collections.unmodifiableMap(staticAliases);
    }

    /**
     * Removes all URI aliases from the database and persists the listed mappings.
     *
     * @param mappings to persist
     */
    public void replaceAll(Set<URIAliasMapping> mappings) {
        Set<URI> resources = new HashSet<>(mappings.size());
        mappings.stream().map(URIAliasMapping::getResource).forEach(uri -> {
            if (!resources.add(uri)) {
                throw ValidationException.fromValidationError("Duplicate URI alias mapping for Resource <" + uri + ">");
            }
        });
        repository.replaceAll(mappings);
    }
}
