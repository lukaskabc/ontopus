package cz.lukaskabc.ontology.ontopus.plugin.alias.model;

import cz.lukaskabc.ontology.ontopus.core_model.exception.ValidationException;

import java.net.URI;
import java.util.Objects;

public class URIAliasMapping {
    private final URI resource;
    private final URI alias;

    public URIAliasMapping(URI resource, URI alias) {
        if (resource.equals(alias)) {
            throw ValidationException.fromValidationError(
                    "The URI Alias Mapping resource must not match the alias! " + this);
        }
        this.resource = resource;
        this.alias = alias;
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
