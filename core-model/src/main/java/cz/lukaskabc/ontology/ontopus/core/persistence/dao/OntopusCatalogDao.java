package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core.model.OntopusCatalog_;
import cz.lukaskabc.ontology.ontopus.core.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OntopusCatalogDao extends AbstractDao<OntopusCatalogURI, OntopusCatalog> {
    @Autowired
    public OntopusCatalogDao(EntityManager em, DescriptorFactory descriptorFactory) {

        super(
                OntopusCatalog.class,
                OntopusCatalog_.entityClassIRI.toURI(),
                em,
                descriptorFactory.ontologyArtifactCatalog());
    }

    public boolean catalogExists(OntopusCatalogURI uri) {
        Objects.requireNonNull(uri);
        final var query = em.createNativeQuery("ASK FROM ?graph { ?catalog a ?catalogType }", Boolean.class)
                .setParameter("catalog", uri.toURI())
                .setParameter("catalogType", OntopusCatalog_.entityClassIRI)
                .setParameter("graph", entityGraphContext);

        return Boolean.TRUE.equals(resultOrNull(query::getSingleResult));
    }
}
