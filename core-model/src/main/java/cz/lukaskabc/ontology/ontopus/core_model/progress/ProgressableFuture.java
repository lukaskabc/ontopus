package cz.lukaskabc.ontology.ontopus.core_model.progress;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class ProgressableFuture<R> implements Future<R> {
    private static final AtomicReference<@Nullable ProgressDetail> EMPTY_REF = new AtomicReference<>(null);
    private static final ProgressableFuture<?> CANCELLED = ProgressableFuture.wrap(CancelledFuture.instance);

    @SuppressWarnings("unchecked")
    public static <R> ProgressableFuture<R> cancelled() {
        return (ProgressableFuture<R>) CANCELLED;
    }

    public static <R> ProgressableFuture<R> cancelled(@Nullable ProgressDetail progressDetail) {
        if (progressDetail == null) {
            return cancelled();
        }

        @SuppressWarnings("unchecked")
        final Future<R> cancelled = (Future<R>) CancelledFuture.instance;
        return new ProgressableFuture<>(cancelled, new AtomicReference<>(progressDetail));
    }

    public static <R> ProgressableFuture<R> wrap(Future<R> future) {
        return new ProgressableFuture<>(future, EMPTY_REF);
    }

    private final Future<R> future;

    private final AtomicReference<@Nullable ProgressDetail> progressDetailRef;

    public ProgressableFuture(Future<R> future, AtomicReference<@Nullable ProgressDetail> progressDetailRef) {
        this.future = future;
        this.progressDetailRef = progressDetailRef;
    }

    /**
     * Attempts to cancel execution of this task. This method has no effect if the task is already completed or
     * cancelled, or could not be cancelled for some other reason. Otherwise, if this task has not started when
     * {@code cancel} is called, this task should never run. If the task has already started, then the
     * {@code mayInterruptIfRunning} parameter determines whether the thread executing this task (when known by the
     * implementation) is interrupted in an attempt to stop the task.
     *
     * <p>The return value from this method does not necessarily indicate whether the task is now cancelled; use
     * {@link #isCancelled}.
     *
     * @param mayInterruptIfRunning {@code true} if the thread executing this task should be interrupted (if the thread
     *     is known to the implementation); otherwise, in-progress tasks are allowed to complete
     * @return {@code false} if the task could not be cancelled, typically because it has already completed;
     *     {@code true} otherwise. If two or more threads cause a task to be cancelled, then at least one of them
     *     returns {@code true}. Implementations may provide stronger guarantees.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    /**
     * Returns the exception thrown by the task, without waiting.
     *
     * <p>This method is for cases where the caller knows that the task has already completed with an exception.
     *
     * @return the exception thrown by the task
     * @throws IllegalStateException if the task has not completed, the task completed normally, or the task was
     *     cancelled
     * @implSpec The default implementation invokes {@code isDone()} to test if the task has completed. If done and not
     *     cancelled, it invokes {@code get()} and catches the {@code ExecutionException} to obtain the exception.
     * @since 19
     */
    @Override
    public Throwable exceptionNow() {
        return future.exceptionNow();
    }

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    @Override
    public R get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if
     * available.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException if the wait timed out
     */
    @Override
    public R get(long timeout, @NonNull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    public @Nullable ProgressDetail getProgressDetail() {
        return progressDetailRef.get();
    }

    /**
     * Returns {@code true} if this task was cancelled before it completed normally.
     *
     * @return {@code true} if this task was cancelled before it completed
     */
    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    /**
     * Returns {@code true} if this task completed.
     *
     * <p>Completion may be due to normal termination, an exception, or cancellation -- in all of these cases, this
     * method will return {@code true}.
     *
     * @return {@code true} if this task completed
     */
    @Override
    public boolean isDone() {
        return future.isDone();
    }

    /**
     * Returns the computed result, without waiting.
     *
     * <p>This method is for cases where the caller knows that the task has already completed successfully, for example
     * when filtering a stream of Future objects for the successful tasks and using a mapping operation to obtain a
     * stream of results. {@snippet lang = java: results = futures.stream().filter(f -> f.state() ==
     * Future.State.SUCCESS).map(Future::resultNow).toList(); }
     *
     * @return the computed result
     * @throws IllegalStateException if the task has not completed or the task did not complete with a result
     * @implSpec The default implementation invokes {@code isDone()} to test if the task has completed. If done, it
     *     invokes {@code get()} to obtain the result.
     * @since 19
     */
    @Override
    public R resultNow() {
        return future.resultNow();
    }

    /**
     * {@return the computation state}
     *
     * @implSpec The default implementation uses {@code isDone()}, {@code isCancelled()}, and {@code get()} to determine
     *     the state.
     * @since 19
     */
    @Override
    public State state() {
        return future.state();
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
