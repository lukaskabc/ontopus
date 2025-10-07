package cz.lukaskabc.ontology.ontopus.core.service.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OntologyLoadingService;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

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
        return "ontopus.core.service.OrderedImportPipelineService.OntologyLoadingSelectionService.name";
    }

    @Override
    public OntologyLoadingService handleSubmit(FormResult formResult, ImportProcessContext context) {
        OntologyLoadingService service = super.handleSubmit(formResult, context);
        assert context.peekService() == this;
        context.popService(); // pop self
        context.pushService(service);
        return service;
    }
}
