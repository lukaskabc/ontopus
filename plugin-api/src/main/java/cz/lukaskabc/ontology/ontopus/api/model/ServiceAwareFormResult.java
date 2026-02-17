package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;

public record ServiceAwareFormResult(ImportProcessingService<?> service, FormResult formResult) {}
