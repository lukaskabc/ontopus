package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyVersionSeries;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyVersionSeries_;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import java.net.URI;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class OntologyVersionSeriesDao extends AbstractDao<VersionSeriesURI, OntologyVersionSeries> {

    public OntologyVersionSeriesDao(EntityManager em, Validator validator, DescriptorFactory factory) {
        super(
                OntologyVersionSeries.class,
                OntologyVersionSeries_.entityClassIRI.toURI(),
                em,
                validator,
                factory.ontologyVersionSeries(),
                null);
    }

    @Nullable public OntologyVersionSeries findForArtifact(URI ontologyArtifact) {
        if (ontologyArtifact == null) {
            return null;
        }
        return resultOrNull(em.createQuery(
                        """
				SELECT series FROM OntologyVersionSeries series
				WHERE :artifact MEMBER OF series.ontologyArtifacts
				""",
                        OntologyVersionSeries.class)
                .setParameter("artifact", ontologyArtifact)::getSingleResult);
    }
}
