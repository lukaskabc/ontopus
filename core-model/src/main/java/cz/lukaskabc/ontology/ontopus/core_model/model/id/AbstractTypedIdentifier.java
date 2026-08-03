package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.Nullable;
import org.springframework.lang.Contract;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.net.URI;
import java.util.Objects;

@JsonDeserialize(using = TypedIdentifierDeserializer.class)
public abstract class AbstractTypedIdentifier implements TypedIdentifier {
    private final URI uri;

    protected AbstractTypedIdentifier(String uri) {
        this(URI.create(uri));
    }

    protected AbstractTypedIdentifier(URI uri) {
        Objects.requireNonNull(uri);
        this.uri = uri;
    }

    @Contract("null -> false")
    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof AbstractTypedIdentifier that)) return false;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @JsonValue
    @Override
    public String toString() {
        return uri.toString();
    }

    @Override
    public URI toURI() {
        return uri;
    }
}
