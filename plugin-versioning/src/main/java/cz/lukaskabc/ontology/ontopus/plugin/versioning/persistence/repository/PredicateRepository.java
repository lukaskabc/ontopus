package cz.lukaskabc.ontology.ontopus.plugin.versioning.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.plugin.versioning.persistence.dao.PredicateDao;
import org.eclipse.rdf4j.model.Statement;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@Component
public class PredicateRepository {
    private final PredicateDao predicateDao;

    public PredicateRepository(PredicateDao predicateDao) {
        this.predicateDao = predicateDao;
    }

    @Transactional(readOnly = true)
    public Optional<Statement> findStatement(
            @Nullable ResourceURI subject, Collection<URI> predicates, GraphURI context) {
        return predicateDao.findStatement(subject, predicates, context);
    }
}
