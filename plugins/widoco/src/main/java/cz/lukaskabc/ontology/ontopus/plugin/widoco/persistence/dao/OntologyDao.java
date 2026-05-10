package cz.lukaskabc.ontology.ontopus.plugin.widoco.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

@Component
public class OntologyDao {
    private static final Logger log = LogManager.getLogger(OntologyDao.class);
    private final EntityManager entityManager;

    public OntologyDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Resolves {@link Vocabulary#u_p_preferredNamespaceUri} on the ontology version, returns the matched object or
     * {@code ontology URI}.
     *
     * @param versionURI the identifier of the ontology version
     * @return matched object or ontology URI
     */
    public URI findPreferredNamespaceByVersionURI(OntologyVersionURI versionURI) {
        Objects.requireNonNull(versionURI);
        try {
            return entityManager
                    .createNativeQuery("""
					SELECT ?namespace WHERE {
					    GRAPH ?versionArtifactType {
					        ?artifact ?hasVersionIdentifier ?versionURI ;
					            a ?versionArtifactType .
					    }
					    GRAPH ?versionSeriesType {
					        ?series ?hasMember ?artifact ;
					            ?hasOntologyIdentifier ?ontologyURI ;
					            a ?versionSeriesType .
					    }
					    OPTIONAL {
					        GRAPH ?versionURI {
					            ?ontologyURI ?hasPreferredNamespace ?preferredNamespace .
					        }
					    }
					    BIND(COALESCE(?preferredNamespace, ?ontologyURI) AS ?namespace)
					}
					""", URI.class)
                    .setParameter("versionArtifactType", VersionArtifact_.entityClassIRI)
                    .setParameter("hasVersionIdentifier", VersionArtifact_.versionUriPropertyIRI)
                    .setParameter("versionSeriesType", VersionSeries_.entityClassIRI)
                    .setParameter("hasMember", VersionSeries_.membersPropertyIRI)
                    .setParameter("hasOntologyIdentifier", VersionSeries_.ontologyURIPropertyIRI)
                    .setParameter("hasPreferredNamespace", Vocabulary.u_p_preferredNamespaceUri)
                    .setParameter("versionURI", versionURI.toURI())
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            throw AbstractDao.persistenceException(
                    log, "Failed to find a preferred namespace for ontology version " + versionURI, e);
        }
    }
}
