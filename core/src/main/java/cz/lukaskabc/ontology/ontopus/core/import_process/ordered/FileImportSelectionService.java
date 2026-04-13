package cz.lukaskabc.ontology.ontopus.core.import_process.ordered;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ResultHandlingServiceWrapper;
import cz.lukaskabc.ontology.ontopus.core.service.process.FileImportingService;
import cz.lukaskabc.ontology.ontopus.core.service.process.SingleFileSelectionService;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Service
@Order(ImportProcessServiceOrder.DATA_LOADING_SELECTION_SERVICE + 1)
public class FileImportSelectionService implements OrderedImportPipelineService<Void> {
    private final ObjectMapper objectMapper;
    private final FileImportingService fileImportingService;

    public FileImportSelectionService(ObjectMapper objectMapper, FileImportingService fileImportingService) {
        this.objectMapper = objectMapper;
        this.fileImportingService = fileImportingService;
    }

    @Override
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Void handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        context.popService(); // pop self
        final SingleFileSelectionService selectionService = new SingleFileSelectionService(
                objectMapper,
                "ontopus.core.service.OrderedImportPipelineService.FileImportSelectionService",
                fileImportingService::supports);
        context.pushService(new ResultHandlingServiceWrapper<>(selectionService, this::importFile));
        return null;
    }

    private void importFile(Path pathToImport, ImportProcessContext context) {
        final File fileToImport = pathToImport.toFile();
        try {
            fileImportingService.importFiles(List.of(fileToImport), context);
            context.setOntologyFilePath(pathToImport);
        } catch (Exception e) {
            throw new OntopusException("Failed to import files: " + e.getMessage(), e);
        }
    }
}
