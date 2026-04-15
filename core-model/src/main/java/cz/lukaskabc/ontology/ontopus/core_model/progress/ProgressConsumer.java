package cz.lukaskabc.ontology.ontopus.core_model.progress;

import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@FunctionalInterface
public interface ProgressConsumer extends Consumer<@Nullable ProgressDetail> {}
