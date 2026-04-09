package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class Rdf4JAbstractNamespaceURI extends AbstractTypedIdentifier {
    public Rdf4JAbstractNamespaceURI(String uri) {
        super(uri);
    }

    public Rdf4JAbstractNamespaceURI(URI uri) {
        super(uri);
    }
}
