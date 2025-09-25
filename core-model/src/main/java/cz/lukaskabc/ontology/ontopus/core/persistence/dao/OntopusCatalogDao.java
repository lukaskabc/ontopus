package cz.lukaskabc.ontology.ontopus.core.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core.model.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core.model.OntopusCatalog_;
import cz.lukaskabc.ontology.ontopus.core.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core.persistence.DescriptorFactory;
import cz.lukaskabc.ontology.ontopus.core.persistence.identifier.CatalogUriUriGenerator;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class OntopusCatalogDao extends AbstractDao<OntopusCatalogURI, OntopusCatalog> {
    @Autowired
    public OntopusCatalogDao(
            EntityManager em,
            Validator validator,
            DescriptorFactory descriptorFactory,
            CatalogUriUriGenerator uriGenerator) {
        super(
                OntopusCatalog.class,
                OntopusCatalog_.entityClassIRI.toURI(),
                em,
                validator,
                descriptorFactory.ontologyArtifactCatalog(),
                uriGenerator);
    }

    public boolean catalogExists(URI uri) {
        final var query = em.createNativeQuery("ASK FROM ?graph { ?catalog a ?catalogType }", Boolean.class)
                .setParameter("catalog", uri)
                .setParameter("catalogType", OntopusCatalog_.entityClassIRI)
                .setParameter("graph", entityGraphContext);

        return Boolean.TRUE.equals(resultOrNull(query::getSingleResult));
    }
}
