package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core.model.VersionSeries_;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import java.net.URI;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class VersionSeriesDao extends AbstractDao<VersionSeriesURI, VersionSeries> {

    public VersionSeriesDao(EntityManager em, Validator validator, DescriptorFactory factory) {
        super(
                VersionSeries.class,
                VersionSeries_.entityClassIRI.toURI(),
                em,
                validator,
                factory.ontologyVersionSeries(),
                null);
    }

    @Nullable public VersionSeries findForArtifact(URI ontologyArtifact) {
        if (ontologyArtifact == null) {
            return null;
        }
        return resultOrNull(em.createQuery(
                        """
				SELECT series FROM OntologyVersionSeries series
				WHERE :artifact MEMBER OF series.ontologyArtifacts
				""",
                        VersionSeries.class)
                .setParameter("artifact", ontologyArtifact)::getSingleResult);
    }
}
