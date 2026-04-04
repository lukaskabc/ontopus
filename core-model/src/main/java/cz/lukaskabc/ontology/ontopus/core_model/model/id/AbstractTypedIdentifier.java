package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import com.fasterxml.jackson.annotation.JsonValue;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.net.URI;
import java.util.Objects;

@JsonDeserialize(using = TypedIdentifierDeserializer.class)
public abstract class AbstractTypedIdentifier implements TypedIdentifier {
    private final URI uri;

    protected AbstractTypedIdentifier(String uri) {
        this(URI.create(StringUtils.withoutTrailingSlash(uri)));
    }

    protected AbstractTypedIdentifier(URI uri) {
        Objects.requireNonNull(uri);
        this.uri = StringUtils.withoutTrailingSlash(uri);
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
