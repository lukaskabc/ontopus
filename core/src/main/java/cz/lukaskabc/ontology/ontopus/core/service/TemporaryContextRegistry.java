package cz.lukaskabc.ontology.ontopus.core.service;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.lukaskabc.ontology.ontopus.api.service.core.TemporaryContextGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext_;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.DescriptorFactory;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.identifier.TemporaryContextUriGenerator;
import cz.lukaskabc.ontology.ontopus.core_model.util.TimeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Objects;

@NullMarked
@Component
public class TemporaryContextRegistry implements TemporaryContextGenerator {
    private static final Logger log = LogManager.getLogger(TemporaryContextRegistry.class);
    private final EntityManager em;
    private final EntityDescriptor temporaryContextDescriptor;
    private final TemporaryContextUriGenerator uriGenerator;
    private final URI temporaryContextGraph;
    private final TimeProvider timeProvider;

    @Autowired
    public TemporaryContextRegistry(
            EntityManager entityManager,
            DescriptorFactory descriptorFactory,
            TemporaryContextUriGenerator uriGenerator,
            TimeProvider timeProvider) {
        this.em = entityManager;
        this.temporaryContextDescriptor = descriptorFactory.temporaryContext();
        this.uriGenerator = uriGenerator;
        this.timeProvider = timeProvider;
        temporaryContextGraph = temporaryContextDescriptor.getSingleContext().orElseThrow();
    }

    @Transactional
    public void clearAllTemporaryContexts() {
        log.debug("Clearing all temporary contexts from database");
        em.createNativeQuery("""
				SELECT ?id WHERE {
				    GRAPH ?graph {
				        ?id a ?type .
				    }
				}
				""", URI.class)
                .setParameter("graph", temporaryContextGraph)
                .setParameter("type", TemporaryContext_.entityClassIRI)
                .getResultStream()
                .forEach(this::delete);
    }

    @Transactional
    public void delete(TemporaryContextURI contextURI) {
        delete(contextURI.toURI());
    }

    private void delete(URI context) {
        log.debug("Dropping temporary context: {}", context);
        try {
            Objects.requireNonNull(context);
            em.createNativeQuery("DROP GRAPH ?context")
                    .setParameter("context", context)
                    .executeUpdate();
            em.createNativeQuery("DELETE WHERE { GRAPH ?tempContextGraph { ?context ?predicate ?object . }}")
                    .setParameter("tempContextGraph", temporaryContextGraph)
                    .setParameter("context", context)
                    .executeUpdate();
        } catch (Exception e) {
            log.atError().withThrowable(e).log("Failed to drop temporary context {}", context);
        }
    }

    @Transactional
    @Override
    public TemporaryContextURI generate() {
        TemporaryContext tmp = new TemporaryContext();
        tmp.setIdentifier(uriGenerator.generate(tmp));
        tmp.setCreatedAt(timeProvider.getInstant());
        em.persist(tmp, temporaryContextDescriptor);
        return Objects.requireNonNull(tmp.getIdentifier());
    }
}
