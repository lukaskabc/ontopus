package cz.lukaskabc.ontology.ontopus.api.service;

/**
 * Service plugged in the main importing pipeline.
 * Services implementing this interface can be ordered with {@link org.springframework.core.annotation.Order @Order}
 * @param <R> The type of the result of {@link ImportProcessingService}
 */
public interface OrderedImportPipelineService<R> extends ImportProcessingService<R> {

}
