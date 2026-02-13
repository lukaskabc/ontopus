package cz.lukaskabc.ontology.ontopus.core.rest.response;

import java.io.Serializable;
import java.net.URI;

public class EntityResponse implements Serializable {
    private URI uri;
    private URI identifier;

    public URI getIdentifier() {
        return identifier;
    }

    public URI getUri() {
        return uri;
    }

    public void setIdentifier(URI identifier) {
        this.identifier = identifier;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
