package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class VersionSeriesDao extends AbstractDao<VersionSeriesURI, VersionSeries> {

    public VersionSeriesDao(EntityManager em, DescriptorFactory factory) {
        super(VersionSeries.class, VersionSeries_.entityClassIRI.toURI(), em, factory.ontologyVersionSeries());
    }

    @Nullable public VersionSeries findForArtifact(URI ontologyArtifact) {
        if (ontologyArtifact == null) {
            return null;
        }
        return resultOrNull(
                em.createQuery("""
				SELECT series FROM OntologyVersionSeries series
				WHERE :artifact MEMBER OF series.ontologyArtifacts
				""", VersionSeries.class).setParameter("artifact", ontologyArtifact)::getSingleResult);
    }
}
