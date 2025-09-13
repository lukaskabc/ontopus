package cz.lukaskabc.ontology.ontopus.core.service;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.lukaskabc.ontology.ontopus.api.service.core.TemporaryContextGenerator;
import cz.lukaskabc.ontology.ontopus.core.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core.model.TemporaryContext_;
import cz.lukaskabc.ontology.ontopus.core.persistance.DescriptorFactory;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TemporaryContextRegistry implements TemporaryContextGenerator {
    private static final Logger log = LogManager.getLogger(TemporaryContextRegistry.class);
    private final EntityManager em;
    private final EntityDescriptor temporaryContextDescriptor;
    private final URI temporaryContextGraph;

    @Autowired
    public TemporaryContextRegistry(EntityManager entityManager, DescriptorFactory descriptorFactory) {
        this.em = entityManager;
        this.temporaryContextDescriptor = descriptorFactory.temporaryContext();
        temporaryContextGraph = temporaryContextDescriptor.getSingleContext().orElseThrow();
    }

    @Transactional
    public void clearAllTemporaryContexts() {
        log.debug("Clearing all temporary contexts from database");
        // TODO change to SELECT FROM ?graph
        em.createNativeQuery(
                        """
				SELECT ?id WHERE {
				    GRAPH ?graph {
				        ?id a ?type .
				    }
				}
				""",
                        URI.class)
                .setParameter("graph", temporaryContextGraph)
                .setParameter("type", TemporaryContext_.entityClassIRI)
                .getResultStream()
                .forEach(context -> {
                    try {
                        Objects.requireNonNull(context);
                        em.createNativeQuery(
                                        """
								DROP GRAPH ?context;
								DELETE FROM ?tempContextGraph WHERE { ?context ?predicate ?object . }
								""")
                                .setParameter("context", context)
                                .setParameter("tempContextGraph", temporaryContextGraph)
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
        em.persist(tmp, temporaryContextDescriptor);
        return Objects.requireNonNull(tmp.getUri());
    }
}
