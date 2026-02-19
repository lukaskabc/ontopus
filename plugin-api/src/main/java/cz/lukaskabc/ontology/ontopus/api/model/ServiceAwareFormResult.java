package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;

public record ServiceAwareFormResult(ImportProcessingService<?> service, FormResult formResult) {}
