package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.TemporaryContext_;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.stream.Stream;

@Component
public class TemporaryContextDao extends AbstractDao<TemporaryContextURI, TemporaryContext> {
    private static final Logger log = LogManager.getLogger(TemporaryContextDao.class);

    public TemporaryContextDao(EntityManager em, DescriptorFactory descriptorFactory) {
        super(TemporaryContext.class, TemporaryContext_.entityClassIRI, em, descriptorFactory.temporaryContext());
    }

    public Stream<TemporaryContextURI> findAll() {
        try {
            return em.createNativeQuery("""
					SELECT ?uri FROM ?graph WHERE {
					    ?uri a ?type .
					}
					""", URI.class)
                    .setParameter("graph", entityGraphContext)
                    .setParameter("type", typeUri)
                    .getResultStream()
                    .map(TemporaryContextURI::new);
        } catch (Exception e) {
            throw AbstractDao.persistenceException(log, "Failed to find all temporary contexts", e);
        }
    }
}
