package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.Rdf4JAbstractNamespaceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class PrefixDeclarationDao extends AbstractDao<Rdf4JAbstractNamespaceURI, PrefixDeclaration> {
    private static final Logger log = LogManager.getLogger(PrefixDeclarationDao.class);

    public PrefixDeclarationDao(EntityManager em) {
        super(PrefixDeclaration.class, PrefixDeclaration_.entityClassIRI, em);
    }

    @Nullable public PrefixDeclaration findByPrefixAndNamespace(String prefix, String namespace) {
        try {
            return resultOrNull(em.createQuery(
                            "SELECT d FROM PrefixDeclaration d WHERE d.prefix = :prefix AND d.namespace = :namespace",
                            PrefixDeclaration.class)
                    .setParameter("prefix", prefix)
                    .setParameter("namespace", namespace)
                    .setMaxResults(1)::getSingleResult);
        } catch (Exception e) {
            throw persistenceException(log, "Failed to find prefix declaration for prefix " + prefix, e);
        }
    }

    public void removeOrphans() {
        try {
            em.createNativeQuery("""
					DELETE {
					  GRAPH ?context {
					    ?decl ?predicate ?object .
					  }
					}
					WHERE {
					  GRAPH ?context {
					    ?decl a ?type ;
					      ?predicate ?object .
					  }
					  FILTER NOT EXISTS {
					    ?s ?p ?decl .
					  }
					}
					""")
                    .setParameter("context", entityGraphContext)
                    .setParameter("type", typeUri)
                    .executeUpdate();
        } catch (Exception e) {
            throw persistenceException(log, "Failed to remove prefix declaration orphans", e);
        }
    }
}
