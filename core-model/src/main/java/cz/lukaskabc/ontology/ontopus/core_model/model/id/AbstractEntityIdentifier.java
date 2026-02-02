package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;
import java.util.Objects;

public abstract class AbstractEntityIdentifier implements EntityIdentifier {
    private final URI uri;

    protected AbstractEntityIdentifier(String uri) {
        this(URI.create(uri));
    }

    protected AbstractEntityIdentifier(URI uri) {
        Objects.requireNonNull(uri);
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractEntityIdentifier that)) return false;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    @Override
    public URI toURI() {
        return uri;
    }
}
