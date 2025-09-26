package cz.lukaskabc.ontology.ontopus.api.util;

import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import java.util.function.BiConsumer;
import org.jspecify.annotations.Nullable;

/**
 * Wraps an {@link ImportProcessingService} and calls provided result handler with the result value from the wrapped
 * service.
 *
 * @param <R> The type of the result value
 */
public class ResultHandlingServiceWrapper<R> implements ImportProcessingService<R> {
    private final ImportProcessingService<R> processingService;
    private final BiConsumer<R, ImportProcessContext> resultConsumer;

    public ResultHandlingServiceWrapper(
            ImportProcessingService<R> processingService, BiConsumer<R, ImportProcessContext> resultConsumer) {
        this.processingService = processingService;
        this.resultConsumer = resultConsumer;
    }

    @Override
    public @Nullable JsonForm getJsonForm() {
        return processingService.getJsonForm();
    }

    @Override
    public String getServiceName() {
        return processingService.getServiceName();
    }

    @Override
    public String getUniqueIdentifier() {
        return ResultHandlingServiceWrapper.class.getName() + "---" + processingService.getUniqueIdentifier();
    }

    @Override
    public Result<R> handleSubmit(FormResult formResult, ImportProcessContext context) {
        final Result<R> result = processingService.handleSubmit(formResult, context);
        resultConsumer.accept(result.value(), context);
        return result;
    }
}
