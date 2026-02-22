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
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.jspecify.annotations.Nullable;
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

    /**
     * Deletes all existing mappings of resources from the given source graph. Removed are all mappings to any graph for
     * all resources that are subjects in the source graph.
     *
     * @param sourceGraph the graph for which the existing mappings should be deleted
     */
    public void deleteExistingMappingsForResourcesFrom(GraphURI sourceGraph) {
        try {
            em.createNativeQuery("""
					DELETE {
					    GRAPH ?context {
					        ?subject ?isPartOf ?anyGraph .
					    }
					}
					WHERE {
					    GRAPH ?sourceGraph {
					        ?subject ?p ?o .
					    }
					    GRAPH ?context {
					        ?subject ?isPartOf ?anyGraph .
					    }
					}
					""")
                    .setParameter("context", CONTEXT)
                    .setParameter("isPartOf", Vocabulary.u_p_dcat_isPartOf)
                    .setParameter("sourceGraph", sourceGraph.toURI())
                    .executeUpdate();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Deletes all mappings of resources to the given graph.
     *
     * @param graph the graph for which the mappings should be deleted
     */
    public void deleteMappingForGraph(GraphURI graph) {
        try {
            em.createNativeQuery("""
					               DELETE WHERE {
					                   GRAPH ?context {
					                       ?subject ?isPartOf ?graph .
					                   }
					               }
					""")
                    .setParameter("context", CONTEXT)
                    .setParameter("isPartOf", Vocabulary.u_p_dcat_isPartOf)
                    .setParameter("graph", graph.toURI())
                    .executeUpdate();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Nullable public ResourceInContextMapping find(ResourceURI resource) {
        try {
            return (ResourceInContextMapping) AbstractDao.resultOrNull(
                    em.createNativeQuery("""
					               SELECT ?subject ?object FROM ?context WHERE {
					                   ?subject ?isPartOf ?object .
					               }
					""", ResourceInContextMapping.RESOURCE_IN_CONTEXT_MAPPING)
                            .setParameter("context", CONTEXT)
                            .setParameter("isPartOf", Vocabulary.u_p_dcat_isPartOf)
                            .setParameter("subject", resource.toURI())::getSingleResult);
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
                    .setParameter("isPartOf", Vocabulary.u_p_dcat_isPartOf)
                    .setParameter("object", graph.toURI())
                    .getResultStream();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void mapResourcesFrom(GraphURI sourceGraph) {
        try {
            em.createNativeQuery("""
					               WITH ?context
					               INSERT {
					                   ?subject ?isPartOf ?sourceGraph .
					                   ?sourceGraph ?isPartOf ?sourceGraph .
					               }
					               USING ?sourceGraph
					               WHERE {
					                   ?subject ?p ?o .
					               }
					""")
                    .setParameter("context", CONTEXT)
                    .setParameter("isPartOf", Vocabulary.u_p_dcat_isPartOf)
                    .setParameter("sourceGraph", sourceGraph.toURI())
                    .executeUpdate();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }
}
