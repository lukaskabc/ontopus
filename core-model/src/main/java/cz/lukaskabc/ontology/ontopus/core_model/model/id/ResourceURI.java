package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class ResourceURI extends AbstractTypedIdentifier {
    public ResourceURI(String uri) {
        super(uri);
    }

    public ResourceURI(URI uri) {
        super(uri);
    }
}
