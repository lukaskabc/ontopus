package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;
import java.util.Objects;

public class AbstractTypedIdentifier implements TypedIdentifier {
    private final URI uri;

    protected AbstractTypedIdentifier(String uri) {
        this(URI.create(uri));
    }

    protected AbstractTypedIdentifier(URI uri) {
        Objects.requireNonNull(uri);
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractTypedIdentifier that)) return false;
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
