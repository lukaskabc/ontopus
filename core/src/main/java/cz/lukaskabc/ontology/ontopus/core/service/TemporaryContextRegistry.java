package cz.lukaskabc.ontology.ontopus.core.service;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.api.service.core.TemporaryContextGenerator;
import cz.lukaskabc.ontology.ontopus.core.model.TemporaryContext;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component
public class TemporaryContextRegistry implements TemporaryContextGenerator {
    private final EntityManager em;

    @Autowired
    public TemporaryContextRegistry(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Transactional
    public void clearAllTemporaryContexts() { // TODO call on init?
        em.createQuery("SELECT c.uri FROM TemporaryContext c", URI.class)
                .getResultStream()
                .forEach(context -> {
                    try {
                        em.createNativeQuery(
                                        """
						DROP GRAPH ?context .
						DELETE WHERE {
						    ?context ?subject ?object .
						}
						""")
                                .setParameter("context", Objects.requireNonNull(context))
                                .executeUpdate();
                    } catch (Exception e) {
                        log.error("Failed to drop temporary context {}", context, e);
                    }
                });
    }

    @Transactional
    @Override
    public URI generate() {
        TemporaryContext tmp = new TemporaryContext();
        tmp.setCreatedAt(Instant.now());
        em.persist(tmp);
        return Objects.requireNonNull(tmp.getUri());
    }
}
