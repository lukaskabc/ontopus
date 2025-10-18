package cz.lukaskabc.ontology.ontopus.core.service.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyArtifactBuildingService;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

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
        // keeping self on stack to allow next selection
        context.pushService(service);

        // TODO implement option to stop building the artifact
        // and pop this service from the stack
        return service;
    }
}
