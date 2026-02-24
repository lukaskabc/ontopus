package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;

/** Excludes certain paths (like Swagger UI and API docs) from being handled by the endpoint */
public class CatchAllEndpointIgnoreCondition implements RequestCondition<@NonNull CatchAllEndpointIgnoreCondition> {

    @Override
    public CatchAllEndpointIgnoreCondition combine(CatchAllEndpointIgnoreCondition other) {
        return this;
    }

    @Override
    public int compareTo(CatchAllEndpointIgnoreCondition other, @NonNull HttpServletRequest request) {
        return 0;
    }

    @Override
    @Nullable public CatchAllEndpointIgnoreCondition getMatchingCondition(HttpServletRequest request) {
        String path = request.getRequestURI();

        // If the path belongs to Swagger or springdoc,
        // return null to IGNORE this mapping
        if (path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")) {
            return null;
        }

        // Otherwise, return this condition to ACCEPT the mapping
        return this;
    }
}
