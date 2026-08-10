package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;

/** Negates the result of the wrapped request condition */
public class NagatingRequestCondition implements RequestCondition<NagatingRequestCondition> {
    private final RequestCondition<?> condition;

    public NagatingRequestCondition(RequestCondition<?> condition) {
        this.condition = condition;
    }

    @Override
    public @NonNull NagatingRequestCondition combine(@NonNull NagatingRequestCondition other) {
        throw new UnsupportedOperationException("Combining NotRequestCondition is not supported");
    }

    @Override
    public int compareTo(@NonNull NagatingRequestCondition other, @NonNull HttpServletRequest request) {
        throw new UnsupportedOperationException("Comparing NotRequestCondition is not supported");
    }

    @Override
    public @Nullable NagatingRequestCondition getMatchingCondition(@NonNull HttpServletRequest request) {
        return condition.getMatchingCondition(request) == null ? this : null;
    }
}
