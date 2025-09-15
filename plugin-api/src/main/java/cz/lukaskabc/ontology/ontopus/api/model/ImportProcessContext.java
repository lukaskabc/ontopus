package cz.lukaskabc.ontology.ontopus.api.model;

import cz.lukaskabc.ontology.ontopus.api.service.ImportProcessingService;
import cz.lukaskabc.ontology.ontopus.core.model.OntologyArtifact;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ImportProcessContext {
    private final UUID uuid;
    private final URI databaseContext;
    private final Path tempFolder;
    private final OntologyArtifact ontologyArtifact;
    private final ArrayList<ImportProcessingService<?>> pendingServicesStack;
    private final ArrayList<ImportProcessingService<?>> processedServices;
    private final ArrayList<FormResult> processedResults;

    private final Map<Object, Object> additionalProperties;

    public ImportProcessContext(UUID uuid, URI databaseContext, Path tempFolder, OntologyArtifact ontologyArtifact) {
        this.uuid = uuid;
        this.databaseContext = databaseContext;
        this.tempFolder = tempFolder;
        this.ontologyArtifact = ontologyArtifact;
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

    public URI getDatabaseContext() {
        return databaseContext;
    }

    public OntologyArtifact getOntologyArtifact() {
        return ontologyArtifact;
    }

    public List<ImportProcessingService<?>> getPendingServicesStack() {
        return pendingServicesStack;
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

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Submits the form result to the service at the top of the stack and pops the service.
     *
     * @param formResult the form result to handle
     */
    public void handleResult(FormResult formResult) {
        processedResults.add(formResult);
        popService().handleSubmit(formResult, this);
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
