package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.plugin.versioning.VersionURIConstructionService;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Order(ImportProcessServiceOrder.ARTIFACT_VERSIONING_SELECTION_SERVICE)
public class OntologyVersioningSelectionService extends ImportProcessNextServiceSelector<OntologyVersioningService>
        implements OrderedImportPipelineService<OntologyVersioningService> {
    private final VersionURIConstructionService versionURIConstructionService;

    public OntologyVersioningSelectionService(
            List<OntologyVersioningService> services,
            ObjectMapper objectMapper,
            VersionURIConstructionService versionURIConstructionService) {
        super(services, objectMapper);
        this.versionURIConstructionService = versionURIConstructionService;
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OrderedImportPipelineService.OntologyVersioningSelectionService.name";
    }

    // TODO: Versioning service should respect the version value from the ontology,
    // when its present
    // but it should also offer changing it
    @Override
    public OntologyVersioningService handleSubmit(FormResult formResult, ImportProcessContext context) {
        OntologyVersioningService service = super.handleSubmit(formResult, context);
        assert context.peekService() == this;
        context.popService(); // pop self
        // context.pushService(service);
        context.pushService(versionURIConstructionService);
        return service;
    }
}
