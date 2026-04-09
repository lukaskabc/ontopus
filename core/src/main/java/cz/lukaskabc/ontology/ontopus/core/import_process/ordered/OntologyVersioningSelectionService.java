package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.service.process.ImportProcessNextServiceSelector;
import cz.lukaskabc.ontology.ontopus.core.service.process.VersionURIConstructionService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@Order(ImportProcessServiceOrder.ARTIFACT_VERSIONING_SELECTION_SERVICE)
public class OntologyVersioningSelectionService extends ImportProcessNextServiceSelector<OntologyVersioningService>
        implements OrderedImportPipelineService<OntologyVersioningService> {
    private final VersionURIConstructionService versionURIConstructionService;

    public OntologyVersioningSelectionService(
            List<OntologyVersioningService> services,
            ObjectMapper objectMapper,
            VersionURIConstructionService versionURIConstructionService) {
        super(services, true, objectMapper);
        this.versionURIConstructionService = versionURIConstructionService;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OrderedImportPipelineService.OntologyVersioningSelectionService.name";
    }

    @Override
    public OntologyVersioningService handleSubmit(FormResult formResult, ImportProcessContext context)
            throws JsonFormSubmitException {
        OntologyVersioningService service = super.handleSubmit(formResult, context);
        assert context.peekService() == this;
        context.popService(); // pop self
        context.pushService(versionURIConstructionService);
        context.pushService(service);
        return service;
    }
}
