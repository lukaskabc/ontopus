package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.annotations.ConstructorResult;
import cz.cvut.kbss.jopa.model.annotations.SparqlResultSetMapping;
import cz.cvut.kbss.jopa.model.annotations.VariableResult;
import cz.lukaskabc.ontology.ontopus.core_model.exception.PersistenceException;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.mapping.ResourceInContextMapping;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.stream.Stream;

@SparqlResultSetMapping(
        name = ResourceInContextMapping.RESOURCE_IN_CONTEXT_MAPPING,
        classes = {
            @ConstructorResult(
                    targetClass = ResourceInContextMapping.class,
                    variables = {
                        @VariableResult(name = "subject", type = URI.class),
                        @VariableResult(name = "object", type = URI.class)
                    })
        })
@Component
public class ResourceInContextMappingDao {
    static final URI CONTEXT =
            URI.create("http://ontology.lukaskabc.cz/application/ontopus#ResourceInContextMappingGraph");
    protected final EntityManager em;

    public ResourceInContextMappingDao(EntityManager em) {
        this.em = em;
    }

    public ResourceInContextMapping find(ResourceURI resource) {
        try {
            return (ResourceInContextMapping)
                    em.createNativeQuery("""
					    SELECT ?subject ?object FROM ?context WHERE {
					        ?subject ?isPartOf ?object .
					    }
					""", ResourceInContextMapping.RESOURCE_IN_CONTEXT_MAPPING)
                            .setParameter("context", CONTEXT)
                            .setParameter("isPartOf", Vocabulary.s_p_dcat_isPartOf)
                            .setParameter("subject", resource.toURI())
                            .getSingleResult();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public Stream<?> findAll(GraphURI graph) {
        try {
            return em.createNativeQuery("""
					    SELECT ?subject ?object FROM ?context WHERE {
					        ?subject ?isPartOf ?object .
					    }
					""", ResourceInContextMapping.RESOURCE_IN_CONTEXT_MAPPING)
                    .setParameter("context", CONTEXT)
                    .setParameter("isPartOf", Vocabulary.s_p_dcat_isPartOf)
                    .setParameter("object", graph.toURI())
                    .getResultStream();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }
}
