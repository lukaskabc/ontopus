package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class OntologyVersionURI extends AbstractTypedIdentifier {
    public OntologyVersionURI(String uri) {
        super(uri);
    }

    public OntologyVersionURI(URI uri) {
        super(uri);
    }

    public GraphURI toGraphURI() {
        return new GraphURI(toURI());
    }
}
