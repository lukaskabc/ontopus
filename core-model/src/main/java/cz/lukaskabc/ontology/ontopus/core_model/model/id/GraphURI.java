package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class GraphURI extends AbstractTypedIdentifier {
    public GraphURI(String uri) {
        super(uri);
    }

    public GraphURI(URI uri) {
        super(uri);
    }
}
