package cz.lukaskabc.ontology.ontopus.tests.persistence;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.PersistenceEntity;
import cz.lukaskabc.ontology.ontopus.tests.util.Triple;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public abstract class BaseDaoTest extends DaoTestRunner {
    @Autowired
    private EntityManager em;

    /**
     * Persists given statements
     *
     * @param statements statements to persist
     */
    protected void withData(Collection<Triple> statements) {
        transactional(() -> {
            final Repository repository = em.unwrap(Repository.class);
            final ValueFactory vf = repository.getValueFactory();

            try (RepositoryConnection conn = repository.getConnection()) {
                statements.stream().map(triple -> triple.toStatement(vf)).forEach(conn::add);
            }
        });
    }

    /**
     * Persists all given entities as new entries.
     *
     * @param entities entities to persist
     */
    protected void withEntities(Collection<? extends PersistenceEntity<?>> entities) {
        transactional(() -> entities.forEach(em::persist));
    }
}
