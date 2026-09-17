package cz.lukaskabc.ontology.ontopus.plugin.alias.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.annotations.ConstructorResult;
import cz.cvut.kbss.jopa.model.annotations.SparqlResultSetMapping;
import cz.cvut.kbss.jopa.model.annotations.VariableResult;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import cz.lukaskabc.ontology.ontopus.plugin.alias.model.URIAliasMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;

@SparqlResultSetMapping(
        name = URIAliasMappingDao.URI_ALIAS_MAPPING_NAME,
        classes = {
            @ConstructorResult(
                    targetClass = URIAliasMapping.class,
                    variables = {
                        @VariableResult(name = "resource", type = URI.class),
                        @VariableResult(name = "alias", type = URI.class)
                    })
        })
@Component
public class URIAliasMappingDao {
    public static final String URI_ALIAS_MAPPING_NAME = "URIAliasMapping";
    private static final Logger log = LogManager.getLogger(URIAliasMappingDao.class);

    public static final URI CONTEXT = Vocabulary.u_c_ontopus_URIAliasMapping;

    private final EntityManager em;

    public URIAliasMappingDao(EntityManager em) {
        this.em = em;
    }

    @Nullable public URI findAliasFor(URI resource) {
        return AbstractDao.resultOrNull(em.createNativeQuery("""
				    SELECT ?alias FROM ?context WHERE {
				        ?resource ?hasAlias ?alias .
				    } LIMIT 1
				""", URI.class)
                .setParameter("context", CONTEXT)
                .setParameter("hasAlias", Vocabulary.u_p_ontopus_hasAlias)
                .setParameter("resource", resource)::getSingleResult);
    }

    @SuppressWarnings("unchecked")
    public Stream<URIAliasMapping> findAll() {
        try {
            return em.createNativeQuery("""
					    SELECT ?resource ?alias FROM ?context WHERE {
					        ?resource ?hasAlias ?alias .
					    }
					""", URI_ALIAS_MAPPING_NAME)
                    .setParameter("context", CONTEXT)
                    .setParameter("hasAlias", Vocabulary.u_p_ontopus_hasAlias)
                    .getResultStream();
        } catch (Exception e) {
            throw AbstractDao.persistenceException(log, "Failed to fetch URI alias mappings", e);
        }
    }

    public void removeAll() {
        try {
            em.createNativeQuery("DROP GRAPH ?context")
                    .setParameter("context", CONTEXT)
                    .executeUpdate();
        } catch (Exception e) {
            throw AbstractDao.persistenceException(log, "Failed to remove all URI Alias Mappings", e);
        }
    }

    public void save(URIAliasMapping aliasMapping) {
        Objects.requireNonNull(aliasMapping);
        Objects.requireNonNull(aliasMapping.getResource());
        Objects.requireNonNull(aliasMapping.getAlias());
        try {
            em.createNativeQuery("""
					    DELETE {
					        GRAPH ?context {
					            ?resource ?hasAlias ?anyAlias .
					        }
					    }
					    INSERT {
					        GRAPH ?context {
					            ?resource ?hasAlias ?alias .
					        }
					    } WHERE {
					        OPTIONAL {
					                          GRAPH ?context {
					                              ?resource ?hasAlias ?anyAlias .
					                          }
					        }
					    }
					""")
                    .setParameter("context", CONTEXT)
                    .setParameter("hasAlias", Vocabulary.u_p_ontopus_hasAlias)
                    .setParameter("resource", aliasMapping.getResource())
                    .setParameter("alias", aliasMapping.getAlias())
                    .executeUpdate();
        } catch (Exception e) {
            throw AbstractDao.persistenceException(log, "Failed to save URI Alias Mapping: " + aliasMapping, e);
        }
    }
}
