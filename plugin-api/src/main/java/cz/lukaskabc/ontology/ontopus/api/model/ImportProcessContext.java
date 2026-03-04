package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * State of the import process.
 *
 * <p>No expectations should be put onto contents of {@link #versionArtifact} and {@link #versionSeries}, they could be
 * completely blank (e.g. when publishing a version of new ontology)
 */
@NullMarked
public class ImportProcessContext implements ReadOnlyImportProcessContext {
    /** Series of ontology versions of a single ontology */
    private final VersionSeries versionSeries;
    /** Temporary database context */
    @Nullable private TemporaryContextURI temporaryDatabaseContext;

    private final Path tempFolder;
    /** A new ontology version */
    private final VersionArtifact versionArtifact;

    private final ArrayList<ImportProcessingService<?>> pendingServicesStack;
    private final ArrayList<ImportProcessingService<?>> processedServices;
    private final List<ServiceAwareFormResult> processedResults;

    private final Set<ContextToControllerMapping> controllerMappings;

    // TODO add import process context bootstraper API that will allow to subclass
    // the import process context
    // the hard part will be handling the serialization
    public <P extends ImportProcessContext> ImportProcessContext(P other) {
        this.versionSeries = other.getVersionSeries();
        this.temporaryDatabaseContext = other.getTemporaryDatabaseContext();
        this.tempFolder = other.getTempFolder();
        this.versionArtifact = other.getVersionArtifact();
        this.pendingServicesStack = new ArrayList<>(other.getPendingServicesStack());
        this.processedServices = new ArrayList<>(other.getProcessedServices());
        this.processedResults = new ArrayList<>(other.getProcessedResults());
        this.controllerMappings = new HashSet<>(other.getControllerMappings());
    }

    public ImportProcessContext(
            VersionSeries versionSeries,
            TemporaryContextURI temporaryDatabaseContext,
            Path tempFolder,
            VersionArtifact versionArtifact) {
        this.versionSeries = Objects.requireNonNull(versionSeries);
        this.temporaryDatabaseContext = Objects.requireNonNull(temporaryDatabaseContext);
        this.tempFolder = Objects.requireNonNull(tempFolder);
        this.versionArtifact = Objects.requireNonNull(versionArtifact);
        this.pendingServicesStack = new ArrayList<>();
        this.processedServices = new ArrayList<>();
        this.processedResults = new ArrayList<>();
        this.controllerMappings = new HashSet<>();
    }

    public void addControllerMapping(ContextToControllerMapping mapping) {
        controllerMappings.add(mapping);
    }

    public TemporaryContextURI consumeDatabaseContext() {
        final TemporaryContextURI uri = this.temporaryDatabaseContext;
        this.temporaryDatabaseContext = null;
        return Objects.requireNonNull(uri);
    }

    public Path createTempFolder(Path relativePath) {
        try {
            return Files.createDirectory(tempFolder.resolve(relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    @Override
    public Set<ContextToControllerMapping> getControllerMappings() {
        return Collections.unmodifiableSet(controllerMappings);
    }

    public GraphURI getFinalDatabaseContext() {
        final GraphURI versionUri = versionArtifact.getVersionURI().toGraphURI();
        Objects.requireNonNull(versionUri, "Version URI is not set");
        return versionUri;
    }

    @Override
    public List<ImportProcessingService<?>> getPendingServicesStack() {
        return Collections.unmodifiableList(pendingServicesStack);
    }

    @Override
    public List<ServiceAwareFormResult> getProcessedResults() {
        return Collections.unmodifiableList(processedResults);
    }

    @Override
    public List<ImportProcessingService<?>> getProcessedServices() {
        return Collections.unmodifiableList(processedServices);
    }

    @Override
    public Path getTempFolder() {
        return tempFolder;
    }

    @Override
    public TemporaryContextURI getTemporaryDatabaseContext() {
        return Objects.requireNonNull(temporaryDatabaseContext, "Database context was already consumed");
    }

    @Override
    public VersionArtifact getVersionArtifact() {
        return versionArtifact;
    }

    @Override
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
            processedResults.add(new ServiceAwareFormResult(service, formResult));
            if (service == peekService()) {
                popService();
            }
        } else {
            throw new IllegalStateException(); // TODO exception
        }
    }

    public boolean hasTemporaryDatabaseContext() {
        return temporaryDatabaseContext != null;
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
     * @throws NoSuchElementException if the stack is empty
     */
    public void popService() {
        processedServices.ensureCapacity(processedServices.size() + pendingServicesStack.size());
        ImportProcessingService<?> last = pendingServicesStack.removeLast();
        processedServices.addLast(last);
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
}
