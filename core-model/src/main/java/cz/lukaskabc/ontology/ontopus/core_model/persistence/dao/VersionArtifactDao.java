package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.query.criteria.CriteriaBuilder;
import cz.cvut.kbss.jopa.model.query.criteria.CriteriaQuery;
import cz.cvut.kbss.jopa.model.query.criteria.Root;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.PrefixDeclaration_;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.GraphQuery;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VersionArtifactDao extends AbstractDao<VersionArtifactURI, VersionArtifact> {
    private static final Logger log = LogManager.getLogger(VersionArtifactDao.class);

    @Autowired
    public VersionArtifactDao(EntityManager em) {
        super(VersionArtifact.class, VersionArtifact_.entityClassIRI, em);
    }

    public long count(VersionSeriesURI seriesURI, List<String> filter) {
        return this.count(filter, (query, cb, root) -> {
            filterBySeries(query, cb, root, seriesURI);
        });
    }

    private void filterBySeries(
            CriteriaQuery<?> query, CriteriaBuilder cb, Root<VersionArtifact> root, VersionSeriesURI seriesURI) {
        query.where(cb.equal(root.getAttr("series"), seriesURI.toURI()));
    }

    public List<VersionArtifact> find(VersionSeriesURI seriesURI, Pageable pageable, List<String> filter) {
        return find(pageable, filter, (query, cb, root) -> {
            filterBySeries(query, cb, root, seriesURI);
        });
    }

    /**
     * Finds all triples associated with the given version artifact URI.
     *
     * <p>{@link PrefixDeclaration Prefix declarations} are inlined as Blank Nodes.
     *
     * @param artifactURI the artifact identifier to lookup
     * @return Statements associated with the artifact
     */
    public List<Statement> findAllTriples(VersionArtifactURI artifactURI) {
        return runGraphQuery(conn -> {
            GraphQuery query = conn.prepareGraphQuery("""
					CONSTRUCT {
					    ?artifact ?p ?o .
					    ?artifact ?hasPrefix ?bnode .
					    ?bnode ?declP ?declO .
					}
					WHERE {
					    {
					        GRAPH ?artifactCtx {
					            ?artifact ?p ?o .
					            FILTER(?p != ?hasPrefix)
					        }
					    }
					    UNION
					    {
					        GRAPH ?artifactCtx {
					            ?artifact ?hasPrefix ?decl .
					        }
					        GRAPH ?prefixCtx {
					            ?decl ?declP ?declO .
					            BIND(BNODE(STR(?decl)) AS ?bnode)
					        }
					    }
					} ORDER BY ?bnode ?p ?hasPrefix ?o ?declP ?declO
					""");

            ValueFactory vf = conn.getValueFactory();
            query.setBinding("artifactCtx", vf.createIRI(entityGraphContext.toString()));
            query.setBinding("prefixCtx", vf.createIRI(PrefixDeclaration_.entityClassIRI.toString()));
            query.setBinding("artifact", vf.createIRI(artifactURI.toString()));
            query.setBinding("hasPrefix", vf.createIRI(VersionArtifact_.prefixDeclarationsPropertyIRI.toString()));

            return query;
        });
    }

    public List<VersionArtifact> findByPrevVersion(VersionArtifactURI previousVersion) {
        try {
            return em.createQuery("""
					    SELECT artifact FROM VersionArtifact artifact
					    WHERE artifact.previousVersion = :prev
					""", VersionArtifact.class)
                    .setParameter("prev", previousVersion.toURI())
                    .getResultList();
        } catch (Exception e) {
            throw persistenceException(
                    log, "Failed to find VersionArtifact by previous version: " + previousVersion, e);
        }
    }

    @Nullable public VersionArtifact findByVersionUri(OntologyVersionURI versionURI) {
        try {
            return resultOrNull(em.createQuery(
                            "SELECT artifact FROM VersionArtifact artifact "
                                    + "WHERE artifact.versionUri = :versionUri",
                            VersionArtifact.class)
                    .setParameter("versionUri", versionURI.toURI())::getSingleResult);
        } catch (Exception e) {
            throw AbstractDao.persistenceException(
                    log, "Failed to find version artifact with ontology version URI " + versionURI, e);
        }
    }

    @Nullable public VersionArtifact findOldestFromSeries(VersionSeriesURI seriesURI, VersionArtifactURI excluded) {
        try {
            return resultOrNull(em.createQuery("""
					SELECT artifact FROM VersionArtifact artifact
					    WHERE artifact.series = :series
					        AND artifact.identifier != :excluded
					    ORDER BY artifact.releaseDate ASC
					""", VersionArtifact.class)
                    .setParameter("series", seriesURI.toURI())
                    .setParameter("excluded", excluded.toURI())
                    .setMaxResults(1)::getSingleResult);
        } catch (Exception e) {
            throw persistenceException(log, "Failed to find the oldest version artifact from series " + seriesURI, e);
        }
    }

    /**
     * Returns a sorted list of prefix declarations associated with the given ontology version
     *
     * @param ontologyVersionURI Ontology version to lookup
     * @return sorted list of prefix declarations, sorted by prefix and then by namespace
     */
    public List<PrefixDeclaration> findPrefixDeclarations(OntologyVersionURI ontologyVersionURI) {
        try {
            return em.createQuery("""
					    SELECT artifact.prefixDeclarations
					    FROM VersionArtifact artifact
					    WHERE artifact.versionUri = :iri
					    ORDER BY artifact.prefixDeclarations.prefix, artifact.prefixDeclarations.namespace
					""", PrefixDeclaration.class)
                    .setParameter("iri", ontologyVersionURI.toURI())
                    .getResultList();
        } catch (Exception e) {
            throw AbstractDao.persistenceException(
                    log, "Failed to find prefix declarations for artifact " + ontologyVersionURI, e);
        }
    }
}
