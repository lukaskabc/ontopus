package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class OntologyURI extends ResourceURI implements GraphURI {
    public OntologyURI(String uri) {
        super(uri);
    }

    public OntologyURI(URI uri) {
        super(uri);
    }
}
