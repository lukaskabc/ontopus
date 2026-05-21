package cz.lukaskabc.ontology.ontopus.core.rest.controller;

import cz.lukaskabc.ontology.ontopus.core.service.DcatResourceService;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

@Controller
public class PublicDcatController extends ResourceController {
    public PublicDcatController(
            @Qualifier("dcatResourceService") DcatResourceService dcatResourceService, OntopusConfig config) {
        super(dcatResourceService, config);
    }
}
