package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyAnnotationInjectionService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ResultHandlingServiceWrapper;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.service.GraphService;
import org.eclipse.rdf4j.model.Model;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * Pushes all {@link OntologyAnnotationInjectionService OntologyAnnotationInjectionServices} to the stack and persists
 * all models.
 */
@Order(ImportProcessServiceOrder.ANNOTATION_INJECTION_SERVICE)
@Service
public class AnnotationInjectionService implements OrderedImportPipelineService<Void> {
    private final List<OntologyAnnotationInjectionService> annotationInjectionServices;
    private final GraphService graphService;

    public AnnotationInjectionService(
            List<OntologyAnnotationInjectionService> annotationInjectionServices, GraphService graphService) {
        this.annotationInjectionServices = annotationInjectionServices;
        this.graphService = graphService;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        if (context.peekService() != this) {
            throw new IllegalStateException("Unexpected import process service stack state");
        }
        context.popService(); // pop self
        // push all services to the stack
        for (OntologyAnnotationInjectionService service : annotationInjectionServices) {
            ResultHandlingServiceWrapper<Model> wrapper =
                    new ResultHandlingServiceWrapper<>(service, this::persistModel);
            context.pushService(wrapper);
        }
        return null;
    }

    private void persistModel(Model rdfModel, ImportProcessContext context) {
        if (rdfModel != null) {
            graphService.persistModel(context.getTemporaryDatabaseContext(), rdfModel);
        }
    }
}
