package cz.lukaskabc.ontology.ontopus.api.service.import_process;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReadOnlyImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;

import java.util.function.BiConsumer;

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
    public @Nullable JsonForm getJsonForm(ReadOnlyImportProcessContext context, @Nullable JsonNode previousFormData) {
        return processingService.getJsonForm(context, previousFormData);
    }

    @Override
    public String getServiceName() {
        return processingService.getServiceName();
    }

    @Override
    public String getUniqueContextIdentifier(ReadOnlyImportProcessContext context) {
        return ResultHandlingServiceWrapper.class.getName() + "---"
                + processingService.getUniqueContextIdentifier(context);
    }

    @Override
    public R handleSubmit(FormResult formResult, ImportProcessContext context) throws JsonFormSubmitException {
        final R result = processingService.handleSubmit(formResult, context);
        resultConsumer.accept(result, context);
        return result;
    }
}
