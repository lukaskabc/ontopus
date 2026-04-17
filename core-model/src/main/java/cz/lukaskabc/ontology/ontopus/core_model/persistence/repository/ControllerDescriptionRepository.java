package cz.lukaskabc.ontology.ontopus.core_model.persistence.repository;

import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ControllerDescriptionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.dao.ControllerDescriptionDao;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.base.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Validator;

@Repository
public class ControllerDescriptionRepository
        extends AbstractRepository<ControllerDescriptionURI, ControllerDescription, ControllerDescriptionDao> {
    public ControllerDescriptionRepository(ControllerDescriptionDao dao, Validator validator, OntopusConfig config) {
        super(dao, validator, null, config);
    }
}
