package cz.lukaskabc.ontology.ontopus.core.factory;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessFinalizedException;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessNotInitializedException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.InternalException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusCheckedException;
import cz.lukaskabc.ontology.ontopus.core_model.exception.OntopusException;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.service.TemporaryContextService;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

@SessionScope
@NullMarked
@Component
public class ImportProcessContextHolder implements AutoCloseable {
    private static final String TEMPORARY_FOLDER_PREFIX = "OntoPuS-import-process-";
    private static final Logger log = LogManager.getLogger(ImportProcessContextHolder.class);

    private static boolean isFinalizedFuture(@Nullable Future<?> future) {
        return future != null
                && future.state() == Future.State.FAILED
                && future.exceptionNow() instanceof ImportProcessFinalizedException;
    }

    private Future<?> future;

    @Nullable private ImportProcessContext instance = null;

    private final TemporaryContextService temporaryContextService;
    private final ListableBeanFactory beanFactory;
    private final ExecutorService executor;
    private final VersionSeriesService versionSeriesService;

    /** Files and directories that will be deleted when the context is closed */
    private final Set<File> toDelete = new HashSet<>();

    private final ReentrantLock lock = new ReentrantLock();

    public ImportProcessContextHolder(
            TemporaryContextService temporaryContextService,
            ListableBeanFactory beanFactory,
            ExecutorService executor,
            VersionSeriesService versionSeriesService) {
        this.temporaryContextService = temporaryContextService;
        this.beanFactory = beanFactory;
        this.executor = executor;
        this.versionSeriesService = versionSeriesService;
        this.future = CancelledFuture.getInstance();
    }

    @Override
    public synchronized void close() {
        log.debug("Closing import process context {}", instance);
        for (File file : toDelete) {
            try {
                log.debug("Removing import process context file {}", file::getPath);
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    file.delete();
                }
            } catch (Exception e) {
                log.debug("Failed to delete import process context file {} | {}", file::getPath, e::getMessage);
            }
        }
        toDelete.clear();
        if (instance != null) {
            if (instance.hasTemporaryDatabaseContext()) {
                temporaryContextService.deleteById(instance.getTemporaryDatabaseContext());
            }
            instance = null;
        }
    }

    private ImportProcessContext createNewContext(VersionSeries series) {
        final UUID uuid = UUID.randomUUID();
        final Path tempFolder = createTempFolder(uuid);
        final TemporaryContextURI databaseContext =
                this.temporaryContextService.generate().getIdentifier();
        Objects.requireNonNull(databaseContext, "Generated context must have an identifier");
        final VersionArtifact artifact = new VersionArtifact();
        return new ImportProcessContext(series, databaseContext, tempFolder, artifact);
    }

    private void createServiceStack(ImportProcessContext context) {
        beanFactory.getBeansOfType(OrderedImportPipelineService.class).values().stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE.reversed())
                .forEach(context::pushService);
    }

    /**
     * Creates a temporary folder prefixed with {@link #TEMPORARY_FOLDER_PREFIX} and the {@code uuid}. The folder is
     * valid through the HTTP session.
     *
     * @param uuid The UUID of the import process
     * @return The path of the newly created temporary folder
     */
    private Path createTempFolder(UUID uuid) {
        try {
            final Path folder = Files.createTempDirectory(TEMPORARY_FOLDER_PREFIX + uuid + "_");
            toDelete.add(folder.toFile());
            return folder;
        } catch (IOException e) {
            throw log.throwing(InternalException.fileProcessingException(
                    "Failed to create temporary folder for context " + uuid, e));
        }
    }

    private Runnable decorateAsyncTask(Consumer<ImportProcessContext> consumer) {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return () -> {
            try {
                Objects.requireNonNull(instance, "Import process context not initialized, instance is null");
                RequestContextHolder.setRequestAttributes(requestAttributes);
                SecurityContextHolder.setContext(securityContext);
                consumer.accept(instance);
            } catch (OntopusException | OntopusCheckedException | ImportProcessFinalizedException e) {
                throw e;
            } catch (Exception e) {
                log.error("Unexpected exception occurred during asynchronous task execution: {}", e.getMessage());
                log.throwing(e);
                throw e;
            }
        };
    }

    private void ensureInitializedOrFinalized() {
        if (instance == null && !isFinalizedFuture(future)) {
            throw ImportProcessNotInitializedException.INSTANCE;
        }
    }

    private VersionSeries findByIdOrNew(@Nullable VersionSeriesURI uri) {
        if (uri == null) {
            return new VersionSeries();
        }
        return versionSeriesService.findRequiredById(uri);
    }

    /** Creates a new import process with context. */
    private ImportProcessContext makeImportContext(@Nullable VersionSeriesURI uri) {
        final VersionSeries series = findByIdOrNew(uri);
        final ImportProcessContext context = createNewContext(series);
        final SerializableImportProcessContext serialized = series.getSerializableImportProcessContext();

        createServiceStack(context);

        if (serialized != null) {
            context.setServiceToDefaultFormDataMap(serialized.getServiceToFormResultMap());
        }

        log.debug("Created new import process context {} and folder {}", context, context.getTempFolder());
        return context;
    }

    public void resetSessionImportProcess(@Nullable VersionSeriesURI uri) {
        lock.lock();
        try {
            log.trace("Resetting Session Import Process with version series URI {}", uri);
            this.close();
            this.instance = makeImportContext(uri);
            this.future = CancelledFuture.getInstance();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Synchronously executes the function blocking all other operations on the holder.
     *
     * @param function accepting the context and producing {@code <R>}
     * @return canceled future when there is already another task running or scheduled, completed future with the result
     *     otherwise, failed future when the previous future failed.
     * @param <R> the result type
     */
    public <R extends @Nullable Object> Future<R> runWithContextNow(Function<ImportProcessContext, R> function) {
        lock.lock();
        try {
            ensureInitializedOrFinalized();
            switch (future.state()) {
                case FAILED:
                    Future<R> result = CompletableFuture.failedFuture(future.exceptionNow());
                    future = CancelledFuture.getInstance();
                    return result;
                case CANCELLED, SUCCESS:
                    Objects.requireNonNull(instance);
                    return CompletableFuture.completedFuture(function.apply(instance));
                default:
                    return CancelledFuture.getInstance();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Asynchronously executes the function when there is no other task scheduled or running.
     *
     * @param consumer accepting the context
     * @return canceled future when there is already another task running or scheduled, future of the submitted task
     *     otherwise, failed future when the previous future failed.
     */
    public Future<@Nullable Void> scheduleWithContext(Consumer<ImportProcessContext> consumer) {
        lock.lock();
        try {
            ensureInitializedOrFinalized();
            Future<Void> result;
            switch (future.state()) {
                case FAILED:
                    result = CompletableFuture.failedFuture(future.exceptionNow());
                    future = CancelledFuture.getInstance();
                    return (Future<@Nullable Void>) result;
                case CANCELLED, SUCCESS:
                    result = submitVoidTask(consumer);
                    future = result;
                    return (Future<@Nullable Void>) result;
                default:
                    result = CancelledFuture.getInstance();
            }
            return (Future<@Nullable Void>) result;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private Future<Void> submitVoidTask(Consumer<ImportProcessContext> consumer) {
        return (Future<Void>) executor.submit(decorateAsyncTask(consumer));
    }

    private static final class CancelledFuture<T> implements Future<T> {
        private static final CancelledFuture<?> instance = new CancelledFuture<Void>();

        @SuppressWarnings("unchecked")
        public static <T> CancelledFuture<T> getInstance() {
            return (CancelledFuture<T>) instance;
        }

        private CancelledFuture() {}

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public T get() {
            throw new CancellationException();
        }

        @Override
        public T get(long timeout, TimeUnit unit) {
            throw new CancellationException();
        }

        @Override
        public boolean isCancelled() {
            return true;
        }

        @Override
        public boolean isDone() {
            return true;
        }
    }
}
