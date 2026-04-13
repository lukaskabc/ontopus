package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.service.import_process.OntologyLoadingService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.service.process.ImportProcessNextServiceSelector;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@Order(ImportProcessServiceOrder.DATA_LOADING_SELECTION_SERVICE)
public class OntologyLoadingSelectionService extends ImportProcessNextServiceSelector<OntologyLoadingService>
        implements OrderedImportPipelineService<OntologyLoadingService> {

    public OntologyLoadingSelectionService(
            List<OntologyLoadingService> fileLoadingServices, ObjectMapper objectMapper) {
        super(fileLoadingServices, objectMapper);
    }

    @Override
    public String getServiceName() {
        return getTranslationRoot() + ".title";
    }

    @Override
    protected String getTranslationRoot() {
        return "ontopus.core.service.OrderedImportPipelineService.OntologyLoadingSelectionService";
    }
}
