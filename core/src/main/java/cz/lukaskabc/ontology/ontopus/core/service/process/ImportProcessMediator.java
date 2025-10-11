package cz.lukaskabc.ontology.ontopus.core.service.process;

import com.fasterxml.jackson.databind.JsonNode;
import cz.lukaskabc.ontology.ontopus.api.model.FormResult;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.model.ReusableFile;
import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core.factory.ImportProcessContextHolder;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.rest.dto.ReusableFileDto;
import cz.lukaskabc.ontology.ontopus.core.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core.util.ImportContextUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Future;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportProcessMediator {
    static final String FILE_SERVICE_PREFIX_SEPARATOR = ":";

    private static File copyFileToContext(
            InputStreamSource source, ReusableFileDto fileDto, ImportProcessContext context) {
        Objects.requireNonNull(context.getTempFolder());
        final Path safeDestination = FileUtils.resolvePath(context.getTempFolder(), Path.of(fileDto.getFileName()));

        try {
            switch (fileDto.getType()) {
                case SERVER -> {
                    Files.copy(source.getInputStream(), safeDestination);
                }
                case UPLOAD -> {}
                // TODO exception
                default -> throw new IllegalStateException("Unknown ReusableFile type: " + fileDto.getType());
            }
            ;
            final File safeTarget = safeDestination.toFile();
            if (!safeTarget.isFile()) {
                throw new IllegalStateException("Copied file does not exist: " + safeDestination);
            }
            return safeTarget;
        } catch (IOException e) {
            throw new RuntimeException(e);
            // TODO exception
        }
    }

    /**
     * Copies files to context directory
     *
     * @param reusableFiles files to copy
     * @param context the import context
     * @return Map of relative paths to the files
     */
    private static Map<String, ReusableFile> copyFiles(
            Map<ReusableFileDto, InputStreamSource> reusableFiles, ImportProcessContext context) {
        final Map<String, ReusableFile> files = new HashMap<>(reusableFiles.size());
        for (Map.Entry<ReusableFileDto, InputStreamSource> entry : reusableFiles.entrySet()) {
            ReusableFileDto dto = entry.getKey();
            File file = copyFileToContext(entry.getValue(), dto, context);
            files.put(dto.getFileName(), dto.toReusableFile(file));
        }
        return files;
    }

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
                combinedResult.reusableFiles().entrySet()) {
            String[] names = entry.getKey().split(FILE_SERVICE_PREFIX_SEPARATOR, 2);
            String serviceClassName = names[0];
            String paramName = names[1];
            files.putIfAbsent(serviceClassName, new HashMap<>());
            files.get(serviceClassName).put(paramName, entry.getValue()); // TODO rework handling reusable files with
            // service prefixed names
        }
        return files;
    }

    private static Map<String, Map<String, JsonNode>> extractCombinedParams(Map<String, JsonNode> combinedFormData) {
        Map<String, Map<String, JsonNode>> params = new HashMap<>();
        for (Map.Entry<String, JsonNode> entry : combinedFormData.entrySet()) {
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

    private Map<String, FormResult> extractFormResults(
            Map<String, JsonNode> combinedFormData, Map<String, ReusableFile> filesMap) {
        var paramMap = extractCombinedParams(combinedFormData);
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
     * @param combinedJsonData combined results
     * @return canceled future when there is already a different task scheduled or running, pending future otherwise
     */
    public Future<?> submitCombinedFormResult(
            Map<String, JsonNode> combinedJsonData, Map<ReusableFileDto, InputStreamSource> reusableFiles) {
        return holder.scheduleWithContext((context -> {
            Map<String, ReusableFile> filesMap = copyFiles(reusableFiles, context);
            Map<String, FormResult> formResults = extractFormResults(combinedJsonData, filesMap);
            processAllResults(context, formResults);
        }));
    }

    /**
     * Submits a single result to the service at the top of the service stack.
     *
     * @param jsonData the json data to submit
     * @return canceled future when there is already a different task scheduled or running, pending future otherwise
     */
    public Future<?> submitFormResult(
            Map<String, JsonNode> jsonData, Map<ReusableFileDto, InputStreamSource> reusableFiles) {
        return holder.scheduleWithContext((context) -> {
            if (context.hasUnprocessedService()) {
                Map<String, ReusableFile> filesMap = copyFiles(reusableFiles, context);
                context.handleResult(new FormResult(jsonData, filesMap));
                finalize(context);
            }
        });
    }
}
