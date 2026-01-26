package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class TemporaryContextURI extends AbstractEntityIdentifier {
    public TemporaryContextURI(String uri) {
        super(uri);
    }

    public TemporaryContextURI(URI uri) {
        super(uri);
    }
}
