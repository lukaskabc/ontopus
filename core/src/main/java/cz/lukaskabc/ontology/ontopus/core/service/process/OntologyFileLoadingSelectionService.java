package cz.lukaskabc.ontology.ontopus.core.service.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.FileLoadingService;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(ImportProcessServiceOrder.DATA_LOADING_SELECTION_SERVICE)
public class OntologyFileLoadingSelectionService extends ImportProcessNextServiceSelector<FileLoadingService>
        implements OrderedImportPipelineService<FileLoadingService> {

    public OntologyFileLoadingSelectionService(
            List<FileLoadingService> fileLoadingServices, ObjectMapper objectMapper) {
        super(fileLoadingServices, objectMapper);
    }

    @Override
    public String getServiceName() {
        return "ontopus.core.service.OrderedImportPipelineService.OntologyFileLoadingSelectionService.name";
    }

    @Override
    public FileLoadingService handleSubmit(FormResult formResult, ImportProcessContext context) {
        FileLoadingService service = super.handleSubmit(formResult, context);
        assert context.peekService() == this;
        context.popService(); // pop self
        context.pushService(service);
        return service;
    }
}
