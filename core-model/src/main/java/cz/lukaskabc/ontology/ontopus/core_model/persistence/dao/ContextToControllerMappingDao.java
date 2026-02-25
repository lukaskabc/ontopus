package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ContextToControllerMappingURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping_;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.MappingType;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.DescriptorFactory;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ContextToControllerMappingDao
        extends AbstractDao<ContextToControllerMappingURI, ContextToControllerMapping> {
    public ContextToControllerMappingDao(EntityManager em, DescriptorFactory descriptorFactory) {
        super(
                ContextToControllerMapping.class,
                ContextToControllerMapping_.entityClassIRI.toURI(),
                em,
                descriptorFactory.contextToControllerMapping());
    }

    @Nullable public ContextToControllerMapping findByTypeAndContext(MappingType mappingType, GraphURI graphURI) {
        return resultOrNull(em.createQuery(
                        "SELECT m FROM ContextToControllerMapping m WHERE m.mappingType = :mappingType AND m.subjects IN (:graphURI)",
                        entityClass)
                .setMaxResults(1)
                .setParameter("mappingType", mappingType.getUri())
                .setParameter("graphURI", graphURI.toURI())::getSingleResult);
    }
}
