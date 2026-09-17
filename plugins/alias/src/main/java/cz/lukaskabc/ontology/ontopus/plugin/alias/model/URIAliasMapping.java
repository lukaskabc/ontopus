package cz.lukaskabc.ontology.ontopus.plugin.alias.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;
import cz.lukaskabc.ontology.ontopus.core_model.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

public class URIAliasMapping {
    /**
     * Replaces the {@code HTTP} scheme with {@code HTTPS}, removes fragment and strips trailing slash.
     *
     * @param uri the URI to normalize
     * @return normalized URI
     */
    public static URI normalize(URI uri) {
        Objects.requireNonNull(uri, "URI cannot be null");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
        builder.fragment(null);

        UriComponents comp = builder.build(true);

        if ("https".equalsIgnoreCase(comp.getScheme())) {
            builder.scheme("http");
        }

        builder.replacePath(StringUtils.withoutTrailingSlash(comp.getPath()));
        return builder.build(true).toUri();
    }

    private final URI resource;

    private final URI alias;

    @JsonCreator
    public URIAliasMapping(@JsonProperty("resource") URI resource, @JsonProperty("alias") URI alias) {
        Objects.requireNonNull(resource, "Resource in URI Alias Mapping must not be null");
        Objects.requireNonNull(alias, "Alias in URI Alias Mapping must not be null");

        this.resource = normalize(resource);
        this.alias = normalize(alias);

        if (this.resource.equals(this.alias)) {
            throw ValidationException.fromValidationError(
                    "The URI Alias Mapping resource must not match the alias! " + this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof URIAliasMapping that)) return false;
        return Objects.equals(resource, that.resource) && Objects.equals(alias, that.alias);
    }

    public URI getAlias() {
        return alias;
    }

    public URI getResource() {
        return resource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, alias);
    }

    @Override
    public String toString() {
        return "URIAliasMapping{" + "resource=" + resource + ", alias=" + alias + '}';
    }
}
