package cz.lukaskabc.ontology.ontopus.api.model;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.http.MediaType;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@NullMarked
public class EndpointRegistrationInfo {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static Builder builder() {
        return new Builder();
    }

    protected static void ensureMethodHandlerValid(Object handler, Method handlerMethod) {
        Objects.requireNonNull(handler);
        Objects.requireNonNull(handlerMethod);
        if (!handlerMethod.getDeclaringClass().isAssignableFrom(handler.getClass())) {
            throw new IllegalArgumentException("Handler method not available for handler class "
                    + handlerMethod.getDeclaringClass().getName());
        }
    }

    protected static void ensureNonNullOrBlank(String string) {
        ensureNonNullOrBlank(string, "String");
    }

    protected static void ensureNonNullOrBlank(String string, String objectName) {
        Objects.requireNonNull(string, objectName + " must not be null");
        if (string.isEmpty()) {
            throw new IllegalArgumentException(objectName + " is empty");
        }
    }

    protected static void ensureNonNullOrEmpty(Collection<?> collection, String objectName) {
        Objects.requireNonNull(collection, objectName + " must not be null");
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("Collection " + objectName + " is empty");
        }
    }

    private final String host;

    private final String[] paths;

    private final String[] produces;

    private final Object handler;

    private final Method handlerMethod;

    protected EndpointRegistrationInfo(
            String host, String[] paths, String[] produces, Object handler, Method handlerMethod) {
        this.host = host;
        this.paths = paths;
        this.produces = produces;
        this.handler = handler;
        this.handlerMethod = handlerMethod;
    }

    public Object getHandler() {
        return handler;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public String getHost() {
        return host;
    }

    public String[] getPaths() {
        return paths;
    }

    public String[] getProduces() {
        return produces;
    }

    @NullUnmarked
    public static class Builder {
        private String host;
        private Set<String> paths = Set.of();
        private Set<MediaType> produces = Set.of();
        private Object handler;
        private Method handlerMethod;

        private Builder() {}

        public EndpointRegistrationInfo build() {
            Objects.requireNonNull(host);
            Objects.requireNonNull(paths);
            Objects.requireNonNull(produces);
            Objects.requireNonNull(handler);
            Objects.requireNonNull(handlerMethod);

            ensureMethodHandlerValid(handler, handlerMethod);

            String[] pathArray = paths.toArray(EMPTY_STRING_ARRAY);
            String[] producesArray = new String[produces.size()];

            Iterator<MediaType> iterator = produces.iterator();
            for (int i = 0; i < produces.size(); i++) {
                producesArray[i] = iterator.next().toString();
            }

            return new EndpointRegistrationInfo(host, pathArray, producesArray, handler, handlerMethod);
        }

        public Builder handler(Object handler) {
            Objects.requireNonNull(handler);
            this.handler = handler;
            return this;
        }

        public Builder handlerMethod(Method handlerMethod) {
            Objects.requireNonNull(handlerMethod);
            this.handlerMethod = handlerMethod;
            return this;
        }

        public Builder host(String host) {
            ensureNonNullOrBlank(host, "Host");
            this.host = host;
            return this;
        }

        public Builder paths(Set<String> paths) {
            ensureNonNullOrEmpty(paths, "Paths");
            paths.forEach(EndpointRegistrationInfo::ensureNonNullOrBlank);
            this.paths = paths;
            return this;
        }

        public Builder produces(Set<MediaType> produces) {
            ensureNonNullOrEmpty(produces, "Produces");
            produces.forEach(Objects::requireNonNull);
            this.produces = produces;
            return this;
        }
    }
}
