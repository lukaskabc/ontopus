package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@NullMarked
public class OrRequestCondition implements RequestCondition<OrRequestCondition> {
    final List<RequestCondition<?>> conditions;

    public OrRequestCondition(List<RequestCondition<?>> conditions) {
        this.conditions = conditions;
    }

    public OrRequestCondition(RequestCondition<?>... conditions) {
        this(Arrays.asList(conditions));
    }

    @Override
    public OrRequestCondition combine(OrRequestCondition other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(OrRequestCondition other, HttpServletRequest request) {
        return 0;
    }

    @Override
    public @Nullable OrRequestCondition getMatchingCondition(HttpServletRequest request) {
        for (RequestCondition<?> condition : conditions) {
            Object matchingCondition = condition.getMatchingCondition(request);
            if (matchingCondition != null) {
                return this;
            }
        }
        return null;
    }
}
