package cz.lukaskabc.ontology.ontopus.plugin.versioning.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.plugin.versioning.persistence.repository.PredicateRepository;
import org.eclipse.rdf4j.model.Statement;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@Service
public class PredicateService {
    private final PredicateRepository predicateRepository;

    public PredicateService(PredicateRepository predicateRepository) {
        this.predicateRepository = predicateRepository;
    }

    public Optional<Statement> findStatement(
            @Nullable ResourceURI subject, Collection<URI> predicates, GraphURI context) {
        return predicateRepository.findStatement(subject, predicates, context);
    }
}
