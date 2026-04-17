package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class VersionSeriesDao extends AbstractDao<VersionSeriesURI, VersionSeries> {
    private static final Logger log = LogManager.getLogger(VersionSeriesDao.class);

    public VersionSeriesDao(EntityManager em, DescriptorFactory factory) {
        super(VersionSeries.class, VersionSeries_.entityClassIRI, em, factory.ontologyVersionSeries());
    }

    @Nullable public VersionSeries findForArtifact(@Nullable VersionArtifactURI ontologyArtifact) {
        if (ontologyArtifact == null) {
            return null;
        }
        return resultOrNull(
                em.createQuery("""
				SELECT series FROM OntologyVersionSeries series
				WHERE :artifact MEMBER OF series.ontologyArtifacts
				""", VersionSeries.class).setParameter("artifact", ontologyArtifact)::getSingleResult);
    }

    /** Checks whether the given ontology identifier exists */
    public boolean isOntologyURI(ResourceURI resourceURI) {
        Objects.requireNonNull(resourceURI, "Resource URI must not be null");
        try {
            return Boolean.TRUE.equals(resultOrNull(em.createNativeQuery("""
					ASK FROM ?context WHERE {
					    ?series ?ontologyIdentifier ?resourceUri
					}
					""", Boolean.class)
                    .setParameter("context", entityGraphContext)
                    .setParameter("ontologyIdentifier", VersionSeries_.ontologyURIPropertyIRI)
                    .setParameter("resourceUri", resourceURI.toURI())::getSingleResult));
        } catch (Exception e) {
            throw AbstractDao.persistenceException(log, "Failed to resolve ontology URI", e);
        }
    }
}
