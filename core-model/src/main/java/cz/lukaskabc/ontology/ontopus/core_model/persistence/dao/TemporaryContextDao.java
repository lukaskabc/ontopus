package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext_;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.stream.Stream;

@Component
public class TemporaryContextDao extends AbstractDao<TemporaryContextURI, TemporaryContext> {
    public TemporaryContextDao(
            Class<TemporaryContext> entityClass, URI typeUri, EntityManager em, Descriptor descriptor) {
        super(entityClass, typeUri, em, descriptor);
    }

    public Stream<TemporaryContext> findAll() {
        try {
            return em.createNativeQuery("""
					SELECT ?uri ?createdAt FROM ?graph WHERE {
					    ?uri a ?type ;
					    ?wasCreatedAt ?createdAt .
					}
					""", TemporaryContext.class)
                    .setParameter("graph", descriptor.getSingleContext().orElseThrow())
                    .setParameter("type", typeUri)
                    .setParameter("wasCreatedAt", TemporaryContext_.createdAtPropertyIRI)
                    .getResultStream();
        } catch (Exception e) {
            throw new PersistenceException("Failed to find all temporary contexts", e);
        }
    }
}
