package cz.lukaskabc.ontology.ontopus.core.model.id;

import java.net.URI;

public class TemporaryContextURI extends AbstractEntityIdentifier {
    public TemporaryContextURI(String uri) {
        super(uri);
    }

    public TemporaryContextURI(URI uri) {
        super(uri);
    }
}
