package cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.service;

import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.repository.URIAliasMappingRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
public class AliasService {
    private final URIAliasMappingRepository repository;

    public AliasService(URIAliasMappingRepository repository) {
        this.repository = repository;
    }

    public URIAliasMapping createMapping(URI resource, URI alias) {
        final URIAliasMapping mapping = new URIAliasMapping(resource, alias);
        repository.save(mapping);
        return mapping;
    }

    public Optional<URI> findAliasFor(URI uri) {
        return repository.findAliasFor(uri);
    }
}
