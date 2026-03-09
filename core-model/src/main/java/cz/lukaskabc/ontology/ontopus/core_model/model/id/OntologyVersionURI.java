package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class OntologyVersionURI extends AbstractTypedIdentifier implements GraphURI {
    public OntologyVersionURI(String uri) {
        super(uri);
    }

    public OntologyVersionURI(URI uri) {
        super(uri);
    }
}
