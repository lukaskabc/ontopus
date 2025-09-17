package cz.lukaskabc.ontology.ontopus.core.model.id;

import java.net.URI;

public abstract class AbstractEntityIdentifier implements EntityIdentifier {
    private final URI uri;

    protected AbstractEntityIdentifier(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI toURI() {
        return uri;
    }
}
