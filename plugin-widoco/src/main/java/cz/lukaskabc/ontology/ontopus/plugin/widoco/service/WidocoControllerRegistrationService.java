package cz.lukaskabc.ontology.ontopus.plugin.widoco.service;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.ControllerDescriptionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription_;
import cz.lukaskabc.ontology.ontopus.core_model.service.ControllerDescriptionService;
import cz.lukaskabc.ontology.ontopus.plugin.widoco.rest.HtmlForwardingController;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WidocoControllerRegistrationService {
    private final Set<ControllerDescription> controllerDescriptions;

    public WidocoControllerRegistrationService(
            ControllerDescriptionService controllerDescriptionService,
            HtmlForwardingController htmlForwardingController) {
        this.controllerDescriptions = constructControllers(controllerDescriptionService, htmlForwardingController);
    }

    private Set<ControllerDescription> constructControllers(
            ControllerDescriptionService controllerDescriptionService,
            HtmlForwardingController htmlForwardingController) {
        final ControllerDescriptionURI uri =
                new ControllerDescriptionURI(ControllerDescription_.entityClassIRI + "_HtmlForwardingController");
        final ControllerDescription description = controllerDescriptionService
                .findById(uri)
                .orElseGet(() -> {
                    final ControllerDescription newController = new ControllerDescription();
                    newController.setIdentifier(uri);
                    newController.setClassName(
                            htmlForwardingController.getClass().getName());
                    newController.setSupportedMediaTypes(htmlForwardingController.getSupportedMediaTypes());
                    controllerDescriptionService.persist(newController);
                    return newController;
                });
        return Set.of(description);
    }

    public Set<ControllerDescription> getControllerDescriptions() {
        return controllerDescriptions;
    }
}
