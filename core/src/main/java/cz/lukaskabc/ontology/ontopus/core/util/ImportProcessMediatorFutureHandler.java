package cz.lukaskabc.ontology.ontopus.core.util;

import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessFinalizedException;
import cz.lukaskabc.ontology.ontopus.core.exception.ImportProcessTaskConflictException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.Future;

@NullMarked
public class ImportProcessMediatorFutureHandler {
    private ImportProcessMediatorFutureHandler() {
        throw new AssertionError();
    }

    /**
     * Immediately returns a response entity based on the current future status.
     *
     * @param future the future to handle
     * @return Response entity based on the future status, the value is never null.
     * @param <T> The type of the result value
     */
    public static ResponseEntity<?> handleFuture(Future<? extends @Nullable Object> future) throws Throwable {
        try {
            return resolveFuture(future);
        } catch (ImportProcessFinalizedException e) {
            return ResponseEntity.created(e.getVersionSeriesURI().toURI()).build();
        }
    }

    private static <T> ResponseEntity<?> resolveFuture(Future<@Nullable T> future) throws Throwable {
        return switch (future.state()) {
            case SUCCESS -> {
                T value = future.resultNow();
                if (value != null) {
                    yield ResponseEntity.ok(value);
                }
                yield ResponseEntity.noContent().build();
            }
            case RUNNING -> ResponseEntity.accepted().build();
            case FAILED -> throw future.exceptionNow();
            case CANCELLED -> throw new ImportProcessTaskConflictException();
        };
    }
}
