package cz.lukaskabc.ontology.ontopus.core.factory;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.import_process.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessFinalizedException;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessNotInitializedException;
import cz.lukaskabc.ontology.ontopus.core.import_process.ContextConsumer;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;
import cz.lukaskabc.ontology.ontopus.core_model.progress.ProgressConsumer;
import cz.lukaskabc.ontology.ontopus.core_model.progress.ProgressDetail;
import cz.lukaskabc.ontology.ontopus.core_model.progress.ProgressableFuture;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
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

    @SuppressWarnings("unchecked")
    private static <R> ProgressableFuture<R> unsafeCastFuture(ProgressableFuture<?> future) {
        return (ProgressableFuture<R>) future;
    }

    private ProgressableFuture<?> future;

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
        this.future = ProgressableFuture.cancelled();
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
            throw new RuntimeException(e); // TODO exception
        }
    }

    private Runnable decorateAsyncTask(
            ContextConsumer consumer, AtomicReference<@Nullable ProgressDetail> progressDetailReference) {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final ProgressConsumer progressConsumer = progressDetailReference::set;

        return () -> {
            try {
                Objects.requireNonNull(instance, "Import process context not initialized, instance is null");
                RequestContextHolder.setRequestAttributes(requestAttributes);
                SecurityContextHolder.setContext(securityContext);
                consumer.accept(instance, progressConsumer);
            } catch (ImportProcessFinalizedException e) {
                throw e; // expected exception
            } catch (RuntimeException e) { // TODO perhaps only for debug?
                log.error("Asynchronous task execution failed with exception: {}", e.getMessage(), e);
                throw log.throwing(e);
            }
        };
    }

    private void ensureInitializedOrFinalized() {
        if (instance == null && !isFinalizedFuture(future)) {
            throw new ImportProcessNotInitializedException();
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
        // TODO event
        return context;
    }

    public void resetSessionImportProcess(@Nullable VersionSeriesURI uri) {
        lock.lock();
        try {
            log.trace("Resetting Session Import Process with version series URI {}", uri);
            this.close();
            this.instance = makeImportContext(uri);
            this.future = ProgressableFuture.cancelled();
        } finally {
            lock.unlock();
        }
    }

    private <R extends @Nullable Object> ProgressableFuture<R> runWithContext(
            Function<ImportProcessContext, ProgressableFuture<R>> contextConsumer) {
        lock.lock();
        try {
            ensureInitializedOrFinalized();
            switch (future.state()) {
                case FAILED:
                    // previous future failed
                    ProgressableFuture<?> previousFuture = future;
                    // clear failed state
                    future = ProgressableFuture.cancelled();
                    // return failed future
                    return unsafeCastFuture(previousFuture);
                case CANCELLED, SUCCESS:
                    Objects.requireNonNull(instance);
                    future = contextConsumer.apply(instance);
                    return unsafeCastFuture(future);
                default:
                    // the future is pending or running, return canceled future with progress of the
                    // running future
                    return ProgressableFuture.cancelled(future.getProgressDetail());
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
    public Future<@Nullable Void> runWithContextAsnyc(ContextConsumer consumer) {
        return runWithContext((context -> submitVoidTask(consumer)));
    }

    /**
     * Synchronously executes the function blocking all other operations on the holder.
     *
     * @param function accepting the context and producing {@code <R>}
     * @return canceled future when there is already another task running or scheduled, completed future with the result
     *     otherwise, failed future when the previous future failed.
     * @param <R> the result type
     */
    public <R extends @Nullable Object> ProgressableFuture<R> runWithContextSync(
            Function<ImportProcessContext, R> function) {
        return runWithContext((context) -> {
            Future<R> completedFuture = CompletableFuture.completedFuture(function.apply(context));
            return ProgressableFuture.wrap(completedFuture);
        });
    }

    @SuppressWarnings("unchecked")
    private ProgressableFuture<Void> submitVoidTask(ContextConsumer consumer) {
        final AtomicReference<@Nullable ProgressDetail> progressDetailReference = new AtomicReference<>(null);
        final Runnable decoratedTask = decorateAsyncTask(consumer, progressDetailReference);
        return new ProgressableFuture<>((Future<Void>) executor.submit(decoratedTask), progressDetailReference);
    }
}
