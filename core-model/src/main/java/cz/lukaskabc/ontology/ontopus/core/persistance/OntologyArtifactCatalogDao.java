package cz.lukaskabc.ontology.ontopus.core.persistance;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifactCatalog;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifactCatalog_;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class OntologyArtifactCatalogDao extends AbstractDao<OntologyArtifactCatalog> {
    @Autowired
    public OntologyArtifactCatalogDao(EntityManager em, Validator validator, DescriptorFactory descriptorFactory) {
        super(
                OntologyArtifactCatalog.class,
                OntologyArtifactCatalog_.entityClassIRI.toURI(),
                em,
                validator,
                descriptorFactory.ontologyArtifactCatalog());
    }

    public boolean catalogExists(URI uri) {
        final var query = em.createNativeQuery("ASK FROM ?graph { ?catalog a ?catalogType }", Boolean.class)
                .setParameter("catalog", uri)
                .setParameter("catalogType", OntologyArtifactCatalog_.entityClassIRI)
                .setParameter("graph", entityGraphContext);

        return Boolean.TRUE.equals(resultOrNull(query::getSingleResult));
    }
}
