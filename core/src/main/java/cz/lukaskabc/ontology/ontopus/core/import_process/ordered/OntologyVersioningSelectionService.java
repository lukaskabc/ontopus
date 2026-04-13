package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyVersioningService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.service.process.ImportProcessNextServiceSelector;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@Order(ImportProcessServiceOrder.ARTIFACT_VERSIONING_SELECTION_SERVICE)
public class OntologyVersioningSelectionService extends ImportProcessNextServiceSelector<OntologyVersioningService>
        implements OrderedImportPipelineService<OntologyVersioningService> {

    public OntologyVersioningSelectionService(List<OntologyVersioningService> services, ObjectMapper objectMapper) {
        super(services, objectMapper);
    }

    @Override
    public String getServiceName() {
        return getTranslationRoot() + ".title";
    }

    @Override
    protected String getTranslationRoot() {
        return "ontopus.core.service.OrderedImportPipelineService.OntologyVersioningSelectionService";
    }
}
