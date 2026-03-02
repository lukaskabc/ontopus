package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;

/** Excludes requests to URI that starts with the provided value */
public class RequestUrlNotStartsWithCondition implements RequestCondition<@NonNull RequestUrlNotStartsWithCondition> {
    private final String startWithURI;

    public RequestUrlNotStartsWithCondition(URI startWithURI) {
        this.startWithURI =
                UriComponentsBuilder.fromUri(startWithURI).build().encode().toUriString();
    }

    @Override
    public RequestUrlNotStartsWithCondition combine(RequestUrlNotStartsWithCondition other) {
        return this;
    }

    @Override
    public int compareTo(RequestUrlNotStartsWithCondition other, @NonNull HttpServletRequest request) {
        return 0;
    }

    @Override
    @Nullable public RequestUrlNotStartsWithCondition getMatchingCondition(HttpServletRequest request) {
        final UriComponents requestedURI = UriComponentsBuilder.fromUriString(
                        request.getRequestURL().toString())
                .build()
                .encode();

        if (requestedURI.toUriString().startsWith(startWithURI)) {
            return null;
        }

        // Otherwise, return this condition to ACCEPT the mapping
        return this;
    }
}
