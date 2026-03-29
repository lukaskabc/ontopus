package cz.lukaskabc.ontology.ontopus.core_model.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.ControllerDescriptionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.persistence.repository.ControllerDescriptionRepository;
import cz.lukaskabc.ontology.ontopus.core_model.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class ControllerDescriptionService
        extends BaseService<ControllerDescriptionURI, ControllerDescription, ControllerDescriptionRepository> {
    public ControllerDescriptionService(ControllerDescriptionRepository repository) {
        super(repository);
    }
}
