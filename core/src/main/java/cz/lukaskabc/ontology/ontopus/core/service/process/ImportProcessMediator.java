package cz.lukaskabc.ontology.ontopus.core.service.process;

import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core.factory.ImportProcessContextHolder;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.util.ImportContextUtils;
import java.util.*;
import java.util.concurrent.Future;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportProcessMediator {
    static final String FILE_SERVICE_PREFIX_SEPARATOR = ":";
    /**
     * Extracts files from combined result and separate them by service class name prefixed to parameter names. The
     * parameter name is expected to have following format {@code full.service.class.Name:ParameterName}
     *
     * @param combinedResult the combined result
     * @return Map from service class name to map of parameter name and file list
     */
    private static Map<String, Map<String, List<MultipartFile>>> extractCombinedFiles(FormResult combinedResult) {
        /// Map from service class names to uploaded files (param name, file list)
        Map<String, Map<String, List<MultipartFile>>> files = new HashMap<>();
        for (Map.Entry<String, List<MultipartFile>> entry :
                combinedResult.submittedFiles().entrySet()) {
            String[] names = entry.getKey().split(FILE_SERVICE_PREFIX_SEPARATOR, 2);
            String serviceClassName = names[0];
            String paramName = names[1];
            files.putIfAbsent(serviceClassName, new HashMap<>());
            files.get(serviceClassName).put(paramName, entry.getValue());
        }
        return files;
    }

    private static Map<String, Map<String, JsonNode>> extractCombinedParams(FormResult combinedResult) {
        Map<String, Map<String, JsonNode>> params = new HashMap<>();
        for (Map.Entry<String, JsonNode> entry : combinedResult.formData().entrySet()) {
            if (entry.getValue().isObject()) {
                Set<Map.Entry<String, JsonNode>> props = entry.getValue().properties();
                params.putIfAbsent(entry.getKey(), new HashMap<>(props.size()));
                Map<String, JsonNode> map = params.get(entry.getKey());
                props.forEach(propEntry -> map.put(propEntry.getKey(), entry.getValue()));
            } else {
                throw new IllegalStateException(); // TODO exception
            }
        }
        return params;
    }

    private final ImportProcessContextHolder holder;
    private final ImportFinalizingService finalizingService;

    public ImportProcessMediator(ImportProcessContextHolder contextHolder, ImportFinalizingService finalizingService) {
        this.holder = contextHolder;
        this.finalizingService = finalizingService;
    }

    private Map<String, FormResult> extractFormResults(FormResult combinedResult) {
        var filesMap = extractCombinedFiles(combinedResult);
        var paramMap = extractCombinedParams(combinedResult);
        Map<String, FormResult> formResults = new HashMap<>(Math.max(filesMap.size(), paramMap.size()));
        Set<String> serviceClassNames = new HashSet<>(filesMap.keySet());
        serviceClassNames.addAll(paramMap.keySet());

        for (String serviceClassName : serviceClassNames) {
            Map<String, JsonNode> params = paramMap.get(serviceClassName);
            Map<String, List<MultipartFile>> files = filesMap.get(serviceClassName);
            formResults.put(serviceClassName, new FormResult(params, MultiValueMap.fromMultiValue(files)));
        }

        return formResults;
    }

    /**
     * Finalize the import process with {@link #finalizingService} when the context has no remaining unprocessed
     * services.
     *
     * <p>Does nothing if there is any remaining unprocessed service in the context.
     *
     * @param context The context to finalize
     */
    private void finalize(ImportProcessContext context) {
        if (!context.hasUnprocessedService()) {
            finalizingService.finalize(context);
        }
    }

    /**
     * Synchronously resolves the JSON form from the service at the top of the service stack.
     *
     * @return resolved future with the JSON form or canceled future when there is already another task scheduled or
     *     running.
     */
    public Future<@Nullable JsonForm> getCurrentForm() {
        return holder.runWithContextNow((context) -> {
            if (context.hasUnprocessedService()) {
                return context.peekService().getJsonForm();
            }
            return null;
        });
    }

    /**
     * Initializes a new import process while discarding any existing one in the current session.
     *
     * @param uri The identifier of existing version series
     */
    public void initialize(@Nullable VersionSeriesURI uri) {
        holder.resetSessionImportProcess(uri);
    }

    private void processAllResults(ImportProcessContext context, Map<String, FormResult> results) {
        int serviceId = 0;
        while (context.hasUnprocessedService()) {
            ImportProcessingService<?> service = context.peekService();
            String serviceName = ImportContextUtils.getIndexedServiceIdentifier(service, serviceId);
            FormResult result = results.get(serviceName);
            if (result == null) {
                throw new IllegalStateException(); // TODO: exception
            }
            context.handleResult(result);
        }
        finalize(context);
    }

    /**
     * Submit all required form results combined and complete the import process in background.
     *
     * @param combinedResult combined results
     * @return canceled future when there is already a different task scheduled or running, pending future otherwise
     */
    public Future<?> submitCombinedFormResult(FormResult combinedResult) {
        Map<String, FormResult> formResults = extractFormResults(combinedResult);
        return holder.scheduleWithContext((context -> processAllResults(context, formResults)));
    }

    /**
     * Submits a single result to the service at the top of the service stack.
     *
     * @param formResult the form result to submit
     * @return canceled future when there is already a different task scheduled or running, pending future otherwise
     */
    public Future<?> submitFormResult(FormResult formResult) {
        return holder.scheduleWithContext((context) -> {
            if (context.hasUnprocessedService()) {
                context.handleResult(formResult);
                finalize(context);
            }
        });
    }
}
