package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;

/** Negates the result of the wrapped request condition */
public class NotRequestCondition implements RequestCondition<NotRequestCondition> {
    private final RequestCondition<?> condition;

    public NotRequestCondition(RequestCondition<?> condition) {
        this.condition = condition;
    }

    @Override
    public NotRequestCondition combine(NotRequestCondition other) {
        throw new UnsupportedOperationException("Combining NotRequestCondition is not supported");
    }

    @Override
    public int compareTo(NotRequestCondition other, HttpServletRequest request) {
        throw new UnsupportedOperationException("Comparing NotRequestCondition is not supported");
    }

    @Override
    public @Nullable NotRequestCondition getMatchingCondition(HttpServletRequest request) {
        return condition.getMatchingCondition(request) == null ? this : null;
    }
}
