package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping_;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ContextToControllerMappingDao
        extends AbstractDao<ContextToControllerMappingURI, ContextToControllerMapping> {
    private static final Logger log = LogManager.getLogger(ContextToControllerMappingDao.class);

    public ContextToControllerMappingDao(EntityManager em) {
        super(ContextToControllerMapping.class, ContextToControllerMapping_.entityClassIRI, em);
    }

    public void deleteBySubject(GraphURI graphURI) {
        try {
            em.createNativeQuery("""
					WITH <?context>
					DELETE {
					    ?entity ?p ?o .
					} WHERE {
					    ?entity a ?type ;
					        ?hasSubject ?subject ;
					        ?p ?o .
					}
					""".replace("?context", entityGraphContext.toString()))
                    .setParameter("type", typeUri)
                    .setParameter("hasSubject", ContextToControllerMapping_.subjectPropertyIRI)
                    .setParameter("subject", graphURI.toURI())
                    .executeUpdate();
        } catch (Exception e) {
            throw persistenceException(log, "Failed to delete ContextToControllerMapping by subject", e);
        }
    }

    @Nullable public ContextToControllerMapping findByTypeAndContext(MappingType mappingType, GraphURI graphURI) {
        return resultOrNull(em.createQuery(
                        "SELECT m FROM ContextToControllerMapping m WHERE m.subject = :graphURI AND m.mappingType = :mappingType",
                        entityClass)
                .setMaxResults(1)
                .setParameter("mappingType", mappingType.getUri())
                .setParameter("graphURI", graphURI.toURI())::getSingleResult);
    }
}
