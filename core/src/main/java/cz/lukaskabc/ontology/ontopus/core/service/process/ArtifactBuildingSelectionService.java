package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyArtifactBuildingService;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Order(ImportProcessServiceOrder.ARTIFACT_BUILDING_SELECTION_SERVICE)
public class ArtifactBuildingSelectionService extends ImportProcessNextServiceSelector<OntologyArtifactBuildingService>
        implements OrderedImportPipelineService<OntologyArtifactBuildingService> {
    public ArtifactBuildingSelectionService(List<OntologyArtifactBuildingService> services, ObjectMapper objectMapper) {
        super(services, objectMapper);
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OrderedImportPipelineService.ArtifactBuildingSelectionService.name";
    }

    @Override
    public OntologyArtifactBuildingService handleSubmit(FormResult formResult, ImportProcessContext context) {
        OntologyArtifactBuildingService service = super.handleSubmit(formResult, context);
        assert context.peekService() == this;
        context.popService(); // pop self
        // TODO: maybe there should be an option to apply more than one service
        context.pushService(service);
        return service;
    }
}
