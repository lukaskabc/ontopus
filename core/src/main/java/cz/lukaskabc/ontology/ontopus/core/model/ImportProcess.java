package cz.lukaskabc.ontology.ontopus.core.model;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import java.util.Stack;

public class ImportProcess {
    private final ImportProcessContext context;
    private final Stack<ImportProcessingService<?>> processingServices;

    public ImportProcess(ImportProcessContext context, Stack<ImportProcessingService<?>> processingServices) {
        this.context = context;
        this.processingServices = processingServices;
    }
}
