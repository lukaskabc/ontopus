package cz.lukaskabc.ontology.ontopus.core.rest.response;

import java.io.Serializable;
import java.net.URI;

public class EntityResponse implements Serializable {
    private final URI uri;
    private final URI identifier;

    public EntityResponse(URI uri, URI identifier) {
        this.uri = uri;
        this.identifier = identifier;
    }

    public URI getIdentifier() {
        return identifier;
    }

    public URI getUri() {
        return uri;
    }
}
