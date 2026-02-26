package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.OntopusCatalog_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
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
}
