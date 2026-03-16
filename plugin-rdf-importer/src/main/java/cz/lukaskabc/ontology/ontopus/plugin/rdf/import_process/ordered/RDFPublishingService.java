package cz.lukaskabc.ontology.ontopus.plugin.rdf.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyPublishingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.Controller;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.ContextToControllerMappingService;
import cz.lukaskabc.ontology.ontopus.plugin.rdf.publishing.RDFController;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.Set;

@Order
@Service
public class RDFPublishingService implements OntologyPublishingService, OrderedImportPipelineService<Void> {
    private final ContextToControllerMappingService mappingService;
    private final RDFController rdfController;
    private final Set<Controller> controllerDescriptions;

    public RDFPublishingService(ContextToControllerMappingService mappingService, RDFController rdfController) {
        this.mappingService = mappingService;
        this.rdfController = rdfController;
        this.controllerDescriptions = controllerDescriptions();
    }

    private Set<Controller> controllerDescriptions() {
        final Controller controller = new Controller();
        controller.setClassName(rdfController.getClass().getName());
        controller.setSupportedMediaTypes(rdfController.getSupportedMediaTypes());
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
        final ContextToControllerMapping ontologyMapping =
                mappingService.createOntologyMapping(context.getFinalDatabaseContext(), controllerDescriptions);
        final ContextToControllerMapping resourceMapping =
                mappingService.createResourceMapping(context.getFinalDatabaseContext(), controllerDescriptions);
        context.addControllerMapping(ontologyMapping);
        context.addControllerMapping(resourceMapping);
        return null;
    }
}
