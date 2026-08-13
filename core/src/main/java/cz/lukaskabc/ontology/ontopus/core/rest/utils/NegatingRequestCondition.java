package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;

/** Negates the result of the wrapped request condition */
public class NegatingRequestCondition implements RequestCondition<NegatingRequestCondition> {
    private final RequestCondition<?> condition;

    public NegatingRequestCondition(RequestCondition<?> condition) {
        this.condition = condition;
    }

    @Override
    public @NonNull NegatingRequestCondition combine(@NonNull NegatingRequestCondition other) {
        throw new UnsupportedOperationException("Combining NotRequestCondition is not supported");
    }

    @Override
    public int compareTo(@NonNull NegatingRequestCondition other, @NonNull HttpServletRequest request) {
        throw new UnsupportedOperationException("Comparing NotRequestCondition is not supported");
    }

    @Override
    public @Nullable NegatingRequestCondition getMatchingCondition(@NonNull HttpServletRequest request) {
        return condition.getMatchingCondition(request) == null ? this : null;
    }
}
