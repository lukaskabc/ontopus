package cz.lukaskabc.ontology.ontopus.core.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.progress.ProgressConsumer;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ContextConsumer extends BiConsumer<ImportProcessContext, ProgressConsumer> {}
