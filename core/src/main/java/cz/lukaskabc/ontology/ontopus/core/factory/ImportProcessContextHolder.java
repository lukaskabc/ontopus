package cz.lukaskabc.ontology.ontopus.core.factory;

import cz.lukaskabc.ontology.ontopus.api.model.ImportProcessContext;
import cz.lukaskabc.ontology.ontopus.api.service.OrderedImportPipelineService;
import cz.lukaskabc.ontology.ontopus.api.service.core.TemporaryContextGenerator;
import cz.lukaskabc.ontology.ontopus.core.model.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@NullMarked
@Component
public class ImportProcessContextHolder {
    private static final String TEMPORARY_FOLDER_PREFIX = "OntoPuS-import-process-";

    @SuppressWarnings("unchecked")
    private static <R> Future<R> cancelledFuture() { // TODO move to external class
        return (Future<R>) CancelledFuture.instance;
    }

    /**
     * Creates a temporary folder prefixed with {@link #TEMPORARY_FOLDER_PREFIX} and the {@code uuid}. The folder will
     * be automatically deleted on JVM exit.
     *
     * @param uuid The UUID of the import process
     * @return The path of the newly created temporary folder
     * @see File#deleteOnExit()
     */
    private static Path createTempFolder(UUID uuid) {
        try {
            final Path folder = Files.createTempDirectory(TEMPORARY_FOLDER_PREFIX + uuid + "_");
            folder.toFile().deleteOnExit();
            return folder;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO exception
        }
    }

    private Future<?> future;
    private ImportProcessContext instance;
    private final TemporaryContextGenerator contextGenerator;
    private final ListableBeanFactory beanFactory;
    private final SchedulingTaskExecutor executor;

    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    public ImportProcessContextHolder(
            TemporaryContextGenerator contextGenerator,
            ListableBeanFactory beanFactory,
            SchedulingTaskExecutor executor) {
        this.contextGenerator = contextGenerator;
        this.beanFactory = beanFactory;
        this.executor = executor;
        this.future = CompletableFuture.completedFuture(null);
        resetSessionImportProcess();
    }

    /** Creates a new import process with context. */
    private ImportProcessContext create() {
        final UUID uuid = UUID.randomUUID();
        final Path tempFolder = createTempFolder(uuid);
        final TemporaryContextURI databaseContext = this.contextGenerator.generate();
        final VersionArtifact artifact = new VersionArtifact();

        // TODO: null series needs to be derived from the start of the import process
        // either by specifying an existing ontology and publishing a new version
        // or creating a brand new one
        final ImportProcessContext context = new ImportProcessContext(null, databaseContext, tempFolder, artifact);
        createServiceStack(context);
        return context;
    }

    private void createServiceStack(ImportProcessContext context) {
        beanFactory.getBeansOfType(OrderedImportPipelineService.class).values().forEach(context::pushService);
    }

    public void resetSessionImportProcess() {
        this.instance = create();
    }

    /**
     * Synchronously executes the function blocking all other operations on the holder.
     *
     * @param function accepting the context and producing {@code <R>}
     * @return canceled future when there is already another task running or scheduled, completed future with the result
     *     otherwise
     * @param <R> the result type
     */
    @NullUnmarked
    public <R> Future<R> runWithContextNow(Function<ImportProcessContext, R> function) {
        lock.lock();
        try {
            if (future.isDone()) {
                return CompletableFuture.completedFuture(function.apply(instance));
            }
            return cancelledFuture();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Asynchronously executes the function when there is no other task scheduled or running.
     *
     * @param consumer accepting the context
     * @return canceled future when there is already another task running or scheduled, future of the submitted task
     *     otherwise
     */
    @NullUnmarked
    public Future<?> scheduleWithContext(Consumer<ImportProcessContext> consumer) {
        lock.lock();
        try {
            if (future.isDone()) {
                final Future<?> result = executor.submit(() -> consumer.accept(instance));
                future = result;
                return result;
            }
            return cancelledFuture();
        } finally {
            lock.unlock();
        }
    }

    private static final class CancelledFuture implements Future<Void> {
        private static final CancelledFuture instance = new CancelledFuture();

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public Void get() {
            throw new CancellationException();
        }

        @Override
        public Void get(long timeout, TimeUnit unit) {
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
