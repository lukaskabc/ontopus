package cz.lukaskabc.ontology.ontopus.core.rest.utils;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.lang.Contract;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collection;
import java.util.List;

/** Accepts request if the requested URL starts with specified prefix */
@NullMarked
public class RequestUrlStartsWith extends AbstractRequestCondition<RequestUrlStartsWith> {
    private final String prefix;

    public RequestUrlStartsWith(URI prefix) {
        this.prefix = prefix.toString();
    }

    @Contract("_ -> fail")
    @Override
    public RequestUrlStartsWith combine(RequestUrlStartsWith other) {
        throw new UnsupportedOperationException("Combining RequestUrlStartsWith conditions is not supported");
    }

    @Override
    public int compareTo(RequestUrlStartsWith other, HttpServletRequest request) {
        return 0;
    }

    @Override
    protected Collection<?> getContent() {
        return List.of(prefix);
    }

    @Override
    public @Nullable RequestUrlStartsWith getMatchingCondition(HttpServletRequest request) {
        final URI requested = URI.create(request.getRequestURL().toString());
        if (requested.toString().startsWith(prefix)) {
            return this;
        }
        return null;
    }

    @Override
    protected String getToStringInfix() {
        return " && ";
    }
}
