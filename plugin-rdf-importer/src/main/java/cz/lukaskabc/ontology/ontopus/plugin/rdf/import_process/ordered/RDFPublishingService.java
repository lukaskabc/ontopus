package cz.lukaskabc.ontology.ontopus.plugin.rdf.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyPublishingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ControllerDescriptionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription_;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.core_model.service.ControllerDescriptionService;
import cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing.RDFController;
import org.jspecify.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.Set;

@Order(Ordered.LOWEST_PRECEDENCE)
@Service
public class RDFPublishingService implements OntologyPublishingService, OrderedImportPipelineService<Void> {
    private final ContextToControllerMappingService mappingService;
    private final RDFController rdfController;
    private final Set<ControllerDescription> controllerDescriptions;
    private final ControllerDescriptionService controllerDescriptionService;

    public RDFPublishingService(
            ContextToControllerMappingService mappingService,
            RDFController rdfController,
            ControllerDescriptionService controllerDescriptionService) {
        this.mappingService = mappingService;
        this.rdfController = rdfController;
        this.controllerDescriptionService = controllerDescriptionService;
        this.controllerDescriptions = controllerDescriptions();
    }

    private Set<ControllerDescription> controllerDescriptions() {
        final ControllerDescriptionURI identifier =
                new ControllerDescriptionURI(ControllerDescription_.entityClassIRI + "_pluginRdfController");

        final ControllerDescription controller = controllerDescriptionService
                .findById(identifier)
                .orElseGet(() -> {
                    final ControllerDescription newController = new ControllerDescription();
                    newController.setIdentifier(identifier);
                    newController.setClassName(rdfController.getClass().getName());
                    newController.setSupportedMediaTypes(rdfController.getSupportedMediaTypes());
                    controllerDescriptionService.persist(newController);
                    return newController;
                });
        return Set.of(controller);
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return "RDF Publishing Service";
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) {
        final ContextToControllerMapping ontologyMapping = mappingService.createOntologyMapping(
                context.getFinalDatabaseContext(), controllerDescriptions, context.getControllerMappings());
        final ContextToControllerMapping resourceMapping = mappingService.createResourceMapping(
                context.getFinalDatabaseContext(), controllerDescriptions, context.getControllerMappings());
        context.addControllerMapping(ontologyMapping);
        context.addControllerMapping(resourceMapping);
        return null;
    }
}
