package cz.lukaskabc.ontology.ontopus.core_model.persistence.dao;

import cz.cvut.kbss.jopa.model.EntityManager;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ControllerDescriptionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription_;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.base.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class ControllerDescriptionDao extends AbstractDao<ControllerDescriptionURI, ControllerDescription> {
    public ControllerDescriptionDao(EntityManager em, DescriptorFactory descriptorFactory) {
        super(
                ControllerDescription.class,
                ControllerDescription_.entityClassIRI,
                em,
                descriptorFactory.controllerDescription());
    }
}
