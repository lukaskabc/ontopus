package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.api.exception.JsonFormSubmitException;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ContextToControllerMapping;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormDataDto;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.FormResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger log = LogManager.getLogger(ImportProcessContext.class);
    /** Series of ontology versions of a single ontology */
    private final VersionSeries versionSeries;
    /** Temporary database context */
    @Nullable private TemporaryContextURI temporaryDatabaseContext;

    private final Path tempFolder;
    /** A new ontology version */
    private final VersionArtifact versionArtifact;

    private final ArrayList<ImportProcessingService<?>> pendingServicesStack;
    private final ArrayList<ImportProcessingService<?>> processedServices;
    private final Set<String> processedServiceIdentifiers;
    private final List<ServiceAwareFormResult> processedResults;
    private final Map<Object, Object> additionalProperties;

    private final Set<ContextToControllerMapping> controllerMappings;

    /** Service identifier to default form data mapping. */
    private Map<String, FormDataDto> serviceToDefaultFormDataMap = Map.of();

    @Nullable private Path ontologyFilePath;

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
        this.processedServiceIdentifiers = new HashSet<>(other.getProcessedServiceIdentifiers());
        this.processedResults = new ArrayList<>(other.getProcessedResults());
        this.controllerMappings = new HashSet<>(other.getControllerMappings());
        this.additionalProperties = new HashMap<>(other.getAdditionalProperties());
        this.ontologyFilePath = other.getOntologyFilePath();
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
        this.processedServiceIdentifiers = new HashSet<>();
        this.processedResults = new ArrayList<>();
        this.controllerMappings = new HashSet<>();
        this.additionalProperties = new HashMap<>();
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

    protected Map<Object, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @Override
    public <T> Optional<T> getAdditionalProperty(Object key, Class<T> type) {
        Object value = additionalProperties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        }
        throw new IllegalStateException("Additional property %s is not of type %s".formatted(key, type.getName()));
    }

    @Override
    public Set<ContextToControllerMapping> getControllerMappings() {
        return Collections.unmodifiableSet(controllerMappings);
    }

    public GraphURI getFinalDatabaseContext() {
        final GraphURI versionUri = versionArtifact.getVersionURI();
        Objects.requireNonNull(versionUri, "Version URI is not set");
        return versionUri;
    }

    @Override
    public @Nullable Path getOntologyFilePath() {
        return ontologyFilePath;
    }

    @Override
    public List<ImportProcessingService<?>> getPendingServicesStack() {
        return Collections.unmodifiableList(pendingServicesStack);
    }

    @Override
    public List<ServiceAwareFormResult> getProcessedResults() {
        return Collections.unmodifiableList(processedResults);
    }

    public Set<String> getProcessedServiceIdentifiers() {
        return processedServiceIdentifiers;
    }

    @Override
    public List<ImportProcessingService<?>> getProcessedServices() {
        return Collections.unmodifiableList(processedServices);
    }

    public Map<String, FormDataDto> getServiceToDefaultFormDataMap() {
        return serviceToDefaultFormDataMap;
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
            try {
                service.handleSubmit(formResult, this);
                processedResults.add(new ServiceAwareFormResult(service, formResult));
                if (service == peekService()) {
                    popService();
                }
            } catch (JsonFormSubmitException e) {
                log.error("Failed to process import form result: {}", e.getMessage());
                // TODO: show error to the user
                throw e.asRuntimeException();
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
     * @throws IllegalStateException if the service identifier is already processed
     */
    public void popService() {
        final String identifier = pendingServicesStack.getLast().getUniqueContextIdentifier(this);
        if (processedServiceIdentifiers.add(identifier)) {
            processedServices.ensureCapacity(processedServices.size() + pendingServicesStack.size());
            ImportProcessingService<?> last = pendingServicesStack.removeLast();
            processedServices.addLast(last);
            return;
        }
        throw new IllegalStateException("Duplicated service identifier %s in a import process".formatted(identifier));
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

    public void setOntologyFilePath(@Nullable Path ontologyFilePath) {
        this.ontologyFilePath = ontologyFilePath;
    }

    public void setServiceToDefaultFormDataMap(Map<String, FormDataDto> serviceToDefaultFormDataMap) {
        this.serviceToDefaultFormDataMap = serviceToDefaultFormDataMap;
    }
}
