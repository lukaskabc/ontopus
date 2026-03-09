package cz.lukaskabc.ontology.ontopus.core_model.model.id;

public class GraphURIImpl extends AbstractTypedIdentifier implements GraphURI {
    public GraphURIImpl(String uri) {
        super(uri);
    }

    public GraphURIImpl(java.net.URI uri) {
        super(uri);
    }
}
