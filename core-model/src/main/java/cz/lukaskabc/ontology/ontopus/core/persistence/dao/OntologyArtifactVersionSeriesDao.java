package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.util.OntologyArtifactVersionSeries;
import cz.lukaskabc.ontology.ontopus.core.model.util.OntologyArtifactVersionSeries_;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import java.net.URI;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class OntologyArtifactVersionSeriesDao extends AbstractDao<OntologyArtifactVersionSeries> {

    public OntologyArtifactVersionSeriesDao(
            Class<OntologyArtifactVersionSeries> entityClass,
            EntityManager em,
            Validator validator,
            DescriptorFactory factory) {
        super(
                entityClass,
                OntologyArtifactVersionSeries_.entityClassIRI.toURI(),
                em,
                validator,
                factory.ontologyArtifactVersionSeries());
    }

    @Nullable public OntologyArtifactVersionSeries findForArtifact(URI ontologyArtifact) {
        if (ontologyArtifact == null) {
            return null;
        }
        return resultOrNull(em.createQuery(
                        """
				SELECT series FROM OntologyArtifactVersionSeries series
				WHERE :artifact MEMBER OF series.ontologyArtifacts
				""",
                        OntologyArtifactVersionSeries.class)
                .setParameter("artifact", ontologyArtifact)::getSingleResult);
    }
}
