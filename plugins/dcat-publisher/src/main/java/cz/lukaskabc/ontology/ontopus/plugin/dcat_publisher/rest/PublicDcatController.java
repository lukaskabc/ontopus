package cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.rest;

import cz.lukaskabc.ontology.ontopus.core.rest.controller.ResourceController;
import cz.lukaskabc.ontology.ontopus.core_model.config.OntopusConfig;
import cz.lukaskabc.ontology.ontopus.plugin.dcat_publisher.service.DcatResourceService;
import org.springframework.stereotype.Controller;

@Controller
public class PublicDcatController extends ResourceController {
    public PublicDcatController(DcatResourceService dcatResourceService, OntopusConfig config) {
        super(dcatResourceService, config);
    }
}
