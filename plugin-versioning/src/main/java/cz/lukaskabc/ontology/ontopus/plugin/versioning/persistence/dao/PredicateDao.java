package cz.lukaskabc.ontology.ontopus.plugin.versioning.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@Component
public class PredicateDao {
    private final EntityManager em;

    public PredicateDao(EntityManager em) {
        this.em = em;
    }

    public Optional<Statement> findStatement(
            @Nullable ResourceURI subject, Collection<URI> predicates, GraphURI context) {
        final Repository repository = em.unwrap(Repository.class);
        final ValueFactory vf = repository.getValueFactory();
        final IRI subjectIri = Optional.ofNullable(subject)
                .map(ResourceURI::toString)
                .map(vf::createIRI)
                .orElse(null);
        final IRI contextIri = vf.createIRI(context.toString());
        try (RepositoryConnection connection = repository.getConnection()) {
            for (URI predicate : predicates) {
                RepositoryResult<Statement> result =
                        connection.getStatements(subjectIri, vf.createIRI(predicate.toString()), null, contextIri);

                if (result.hasNext()) {
                    return Optional.of(result.next());
                }
            }
        }
        return Optional.empty();
    }
}
