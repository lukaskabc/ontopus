package cz.lukaskabc.ontology.ontopus.core.service.process;

import cz.lukaskabc.ontology.ontopus.api.model.FormJsonDataDto;
import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.model.JsonForm;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.api.util.FileUtils;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessFinalizedException;
import cz.lukaskabc.ontology.ontopus.core.factory.ImportProcessContextHolder;
import cz.lukaskabc.ontology.ontopus.core.rest.request.FormFileRequest;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormDataDto;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.UploadedFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

@Service
public class ImportProcessMediator {
    static final CopyOption[] REPLACE_EXISTING_COPY_OPTIONS = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING};
    private static final Logger log = LogManager.getLogger(ImportProcessMediator.class);

    private static UploadedFile copyFile(
            FormFileRequest dto, InputStreamSource streamSource, ImportProcessContext context) {
        File file = copyFileToContext(streamSource, dto, context);
        String relativePath = context.getTempFolder().relativize(file.toPath()).toString();
        dto.setFileName(relativePath);
        return dto.toUploadedFile(file.toPath()); // TODO review File/Path usage
    }

    /**
     * Copies the file from the {@code source} to the context directory. Uploaded files always overwrites existing
     * files. Ensures the supplied file name with relative path is safe.
     *
     * @param source The source of the file
     * @param fileDto The file information
     * @param context The import process context
     * @return File in the context temporary directory
     * @throws IllegalStateException if the supplied file name is not safe (escapes the context directory)
     */
    private static File copyFileToContext(
            InputStreamSource source, FormFileRequest fileDto, ImportProcessContext context) {
        Objects.requireNonNull(context.getTempFolder());
        final Path safeDestination =
                FileUtils.resolvePath(context.getTempFolder(), FileUtils.forceRelativePath(fileDto.getPath()));

        try {
            Files.createDirectories(safeDestination.getParent());
            try (InputStream is = source.getInputStream()) {
                Files.copy(is, safeDestination, REPLACE_EXISTING_COPY_OPTIONS);
            }
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
     * Copies files to context directory and constructs {@link FormFileRequest FormFileRequests} with the references to
     * copied files.
     *
     * @param FormFileRequests files to copy
     * @param context the import context
     * @return Map of relative paths to the files in the context directory
     */
    private static Map<String, UploadedFile> copyFiles(
            Map<FormFileRequest, InputStreamSource> FormFileRequests, ImportProcessContext context) {
        final Map<String, UploadedFile> files = new HashMap<>(FormFileRequests.size());
        for (Map.Entry<FormFileRequest, InputStreamSource> entry : FormFileRequests.entrySet()) {
            FormFileRequest dto = entry.getKey();
            UploadedFile file = copyFile(dto, entry.getValue(), context);
            files.put(file.path(), file);
        }
        return files;
    }

    private final ObjectMapper objectMapper;

    private final ImportProcessContextHolder holder;

    private final ImportFinalizationService finalizingService;

    public ImportProcessMediator(
            ImportProcessContextHolder contextHolder,
            ImportFinalizationService finalizingService,
            ObjectMapper objectMapper) {
        this.holder = contextHolder;
        this.finalizingService = finalizingService;
        this.objectMapper = objectMapper;
    }

    /** Closes the current import process and deletes related data (database context, files). */
    public void closeImportProcess() {
        holder.close();
    }

    private ObjectNode deserializeFormDataDto(FormDataDto formDataDto) {
        ObjectNode data = objectMapper.createObjectNode();
        for (Map.Entry<String, String> entry : formDataDto.entrySet()) {
            data.set(entry.getKey(), objectMapper.readTree(entry.getValue()));
        }
        return objectMapper.valueToTree(data);
    }

    /**
     * Finalize the import process with {@link #finalizingService} when the context has no remaining unprocessed
     * services.
     *
     * <p>Does nothing if there is any remaining unprocessed service in the context.
     *
     * @param context The context to finalize
     */
    private void finalizeImport(ImportProcessContext context) {
        if (!context.hasUnprocessedService()) {
            finalizingService.finalizeImport(context);
            final VersionSeriesURI seriesURI = context.getVersionSeries().getIdentifier();
            Objects.requireNonNull(seriesURI, "Version series URI cannot be null when finalizing import process");
            closeImportProcess();
            throw new ImportProcessFinalizedException(seriesURI);
        }
    }

    private FormResult formDataToFormResult(FormDataDto formDataDto) {
        Map<String, JsonNode> data = new HashMap<>(formDataDto.size());
        for (Map.Entry<String, String> entry : formDataDto.entrySet()) {
            data.put(entry.getKey(), objectMapper.readTree(entry.getValue()));
        }
        return new FormResult(data, Map.of());
    }

    /**
     * Synchronously resolves the JSON form from the service at the top of the service stack.
     *
     * @return resolved future with the JSON form or canceled future when there is already another task scheduled or
     *     running.
     */
    public Future<@Nullable JsonForm> getCurrentForm() {
        return holder.<@Nullable JsonForm>runWithContextNow(this::getCurrentFormUsingContext);
    }

    private @Nullable JsonForm getCurrentFormUsingContext(ImportProcessContext context) {
        if (context.hasUnprocessedService()) {
            final ImportProcessingService<?> service = context.peekService();
            final JsonNode defaultFormData = getDefaultFormData(context, service);
            // TODO: create readonly json node

            return service.getJsonForm(context, defaultFormData);
        }
        return null;
    }

    @Nullable private JsonNode getDefaultFormData(ImportProcessContext context, ImportProcessingService<?> service) {
        final String serviceId = service.getUniqueContextIdentifier(context);
        Objects.requireNonNull(serviceId, "Service unique context identifier cannot be null!");

        final FormDataDto defaultFormData =
                context.getServiceToDefaultFormDataMap().get(serviceId);
        if (defaultFormData == null) {

            return null;
        }
        return deserializeFormDataDto(defaultFormData);
    }

    /**
     * Initializes a new import process while discarding any existing one in the current session.
     *
     * @param uri The identifier of existing version series
     */
    public void initialize(@Nullable VersionSeriesURI uri) {
        holder.resetSessionImportProcess(uri);
        Future<@Nullable Void> scheduled = holder.scheduleWithContext(this::processAutoServices);
        if (scheduled.isCancelled()) {
            throw new IllegalStateException(
                    "Failed to schedule service processing during import context initialization");
        }
    }

    private void processAllResults(
            ImportProcessContext context, SerializableImportProcessContext serializableImportProcessContext) {
        final Map<String, FormDataDto> serviceToFormDataMap =
                serializableImportProcessContext.getServiceToFormResultMap();
        while (context.hasUnprocessedService()) {
            final ImportProcessingService<?> service = context.peekService();
            final String serviceId = service.getUniqueContextIdentifier(context);
            final FormDataDto formDataDto = serviceToFormDataMap.get(serviceId);
            if (formDataDto == null) {
                throw log.throwing(new IllegalStateException("Missing form data for service with id: " + serviceId));
            }
            final FormResult formResult = formDataToFormResult(formDataDto);
            context.handleResult(formResult);
            processAutoServices(context);
        }
        finalizeImport(context);
    }

    private void processAutoServices(ImportProcessContext context) {
        while (context.hasUnprocessedService() && context.peekService().getJsonForm(context, null) == null) {
            context.handleResult(new FormResult(Map.of(), Map.of())); // submit empty form result
        }
    }

    /**
     * Submit all required form results combined to complete the import process in background.
     *
     * @return canceled future when there is already a different task scheduled or running, pending future otherwise
     */
    public Future<@Nullable Void> submitCombinedFormResult(SerializableImportProcessContext serializableContext) {
        return holder.scheduleWithContext(context -> processAllResults(context, serializableContext));
    }

    /**
     * Submits a single result to the service at the top of the service stack.
     *
     * @param jsonData the json data to submit
     * @return canceled future when there is already a different task scheduled or running, pending future otherwise
     */
    public Future<@Nullable Void> submitFormResult(
            FormJsonDataDto jsonData, Map<FormFileRequest, InputStreamSource> FormFileRequests) {
        return holder.scheduleWithContext(context -> {
            if (context.hasUnprocessedService()) {
                Map<String, UploadedFile> filesMap = copyFiles(FormFileRequests, context);
                context.handleResult(new FormResult(jsonData, filesMap));
                processAutoServices(context);
                finalizeImport(context);
            }
        });
    }
}
