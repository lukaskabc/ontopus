package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.jspecify.annotations.NullMarked;

/**
 * State of the import process.
 *
 * <p>No expectations should be put onto contents of {@link #versionArtifact} and {@link #versionSeries}, they could be
 * completely blank (e.g. when publishing a version of new ontology)
 */
@NullMarked
public class ImportProcessContext {
    /** Series of ontology versions of a single ontology */
    private final VersionSeries versionSeries;
    /** Temporary database context TODO: when we will transfer data from temporary context to persistent? */
    private final TemporaryContextURI databaseContext;

    private final Path tempFolder;
    /** A new ontology version */
    private final VersionArtifact versionArtifact;

    private final ArrayList<ImportProcessingService<?>> pendingServicesStack;
    private final ArrayList<ImportProcessingService<?>> processedServices;
    private final ArrayList<FormResult> processedResults;

    private final Map<Object, Object> additionalProperties;

    public ImportProcessContext(
            VersionSeries versionSeries,
            TemporaryContextURI databaseContext,
            Path tempFolder,
            VersionArtifact versionArtifact) {
        this.versionSeries = versionSeries;
        this.databaseContext = databaseContext;
        this.tempFolder = tempFolder;
        this.versionArtifact = versionArtifact;
        this.additionalProperties = new HashMap<>();
        this.pendingServicesStack = new ArrayList<>();
        this.processedServices = new ArrayList<>();
        this.processedResults = new ArrayList<>();
    }

    public Map<Object, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public Object getAdditionalProperty(Object key) {
        return additionalProperties.get(key);
    }

    public TemporaryContextURI getDatabaseContext() {
        return databaseContext;
    }

    public List<ImportProcessingService<?>> getPendingServicesStack() {
        return pendingServicesStack;
    }

    public ArrayList<FormResult> getProcessedResults() {
        return processedResults;
    }

    public List<ImportProcessingService<?>> getProcessedServices() {
        return processedServices;
    }

    public Path getTempFolder() {
        return tempFolder;
    }

    public Path getTempFolder(Path relativePath) {
        try {
            return Files.createDirectory(tempFolder.resolve(relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    public VersionArtifact getVersionArtifact() {
        return versionArtifact;
    }

    public VersionSeries getVersionSeries() {
        return versionSeries;
    }

    /**
     * Submits the form result to the service at the top of the stack. The service is popped and the submitted form
     * result is stored when the service handled it without an exception and the same service remain on the stack. The
     * service is kept on the stack and result discarded when an exception was thrown. No service is popped when the
     * service at the top changes, the result is stored either way.
     *
     * @param formResult the form result to handle
     * @throws IllegalStateException when there is no service on the stack
     */
    public void handleResult(FormResult formResult) {
        if (hasUnprocessedService()) {
            ImportProcessingService<?> service = peekService();
            service.handleSubmit(formResult, this);
            processedResults.add(formResult);
            if (service == peekService()) {
                popService();
            }
        } else {
            throw new IllegalStateException(); // TODO exception
        }
    }

    public boolean hasUnprocessedService() {
        return !pendingServicesStack.isEmpty();
    }

    /**
     * Returns the service at the top of the stack.
     *
     * @return the service at the top
     * @throws NoSuchElementException when the stack is empty
     */
    public ImportProcessingService<?> peekService() {
        return pendingServicesStack.getLast();
    }

    /**
     * Removes a service from the top of the service stack and transfers it to the processed services list.
     *
     * @return the removed service
     * @throws NoSuchElementException if the stack is empty
     */
    private ImportProcessingService<?> popService() {
        processedServices.ensureCapacity(processedServices.size() + pendingServicesStack.size());
        ImportProcessingService<?> last = pendingServicesStack.removeLast();
        processedServices.addLast(last);
        return last;
    }

    /**
     * Adds the service to the top of service stack and calls
     * {@link ImportProcessingService#afterStackPush(ImportProcessContext)}.
     *
     * @param service the service to push to the stack
     */
    public void pushService(ImportProcessingService<?> service) {
        pendingServicesStack.addLast(service);
        service.afterStackPush(this);
    }

    public void setAdditionalProperty(Object key, Object value) {
        additionalProperties.put(key, value);
    }
}
