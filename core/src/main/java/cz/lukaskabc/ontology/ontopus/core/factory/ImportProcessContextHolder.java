package cz.lukaskabc.ontology.ontopus.core.factory;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessNotInitializedException;
import cz.lukaskabc.ontology.ontopus.core.service.TemporaryContextRegistry;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.VersionSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.TemporaryContextURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.service.VersionSeriesService;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Future<?> future;

    @Nullable private ImportProcessContext instance = null;

    private final TemporaryContextRegistry temporaryContextRegistry;

    private final ListableBeanFactory beanFactory;
    private final ExecutorService executor;
    private final VersionSeriesService versionSeriesService;
    private final Set<File> toDelete = new HashSet<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    public ImportProcessContextHolder(
            TemporaryContextRegistry temporaryContextRegistry,
            ListableBeanFactory beanFactory,
            ExecutorService executor,
            VersionSeriesService versionSeriesService) {
        this.temporaryContextRegistry = temporaryContextRegistry;
        this.beanFactory = beanFactory;
        this.executor = executor;
        this.versionSeriesService = versionSeriesService;
        this.future = CancelledFuture.getInstance();
    }

    @Override
    public synchronized void close() {
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
            temporaryContextRegistry.delete(instance.getDatabaseContext());
            instance = null;
        }
    }

    /** Creates a new import process with context. */
    private ImportProcessContext create(@Nullable VersionSeriesURI uri) {
        final UUID uuid = UUID.randomUUID();
        final Path tempFolder = createTempFolder(uuid);
        final TemporaryContextURI databaseContext = this.temporaryContextRegistry.generate();
        final VersionArtifact artifact = new VersionArtifact();
        final VersionSeries series = findOrBlankSeries(uri);
        final ImportProcessContext context = new ImportProcessContext(series, databaseContext, tempFolder, artifact);
        createServiceStack(context);
        // TODO event
        return context;
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

    private Runnable decorateAsyncTask(Consumer<ImportProcessContext> consumer) {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return () -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            SecurityContextHolder.setContext(securityContext);
            assert instance != null;
            consumer.accept(instance);
        };
    }

    private void ensureInitialized() {
        if (instance == null) {
            throw new ImportProcessNotInitializedException();
        }
    }

    private VersionSeries findOrBlankSeries(@Nullable VersionSeriesURI uri) {
        VersionSeries series = versionSeriesService.find(uri);
        if (series == null) {
            return new VersionSeries();
        }
        return series;
    }

    public void resetSessionImportProcess(@Nullable VersionSeriesURI uri) {
        lock.lock();
        try {
            log.trace("Resetting Session Import Process");
            this.close();
            this.instance = create(uri);
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
    @NullUnmarked
    public <R> Future<R> runWithContextNow(Function<ImportProcessContext, R> function) {
        lock.lock();
        try {
            ensureInitialized();
            switch (future.state()) {
                case FAILED:
                    Future<R> result = CompletableFuture.failedFuture(future.exceptionNow());
                    future = CancelledFuture.getInstance();
                    return result;
                case CANCELLED, SUCCESS:
                    return CompletableFuture.completedFuture(function.apply(instance));
            }
            return CancelledFuture.getInstance();
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
    @NullUnmarked
    public Future<?> scheduleWithContext(Consumer<ImportProcessContext> consumer) {
        lock.lock();
        try {
            ensureInitialized();
            Future<?> result;
            switch (future.state()) {
                case FAILED:
                    result = CompletableFuture.failedFuture(future.exceptionNow());
                    future = CancelledFuture.getInstance();
                    return result;
                case CANCELLED, SUCCESS:
                    result = executor.submit(decorateAsyncTask(consumer));
                    future = result;
                    return result;
                default:
                    result = CancelledFuture.getInstance();
            }
            return result;
        } finally {
            lock.unlock();
        }
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
