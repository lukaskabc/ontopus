package cz.lukaskabc.ontology.ontopus.core.model.id;

import java.net.URI;

public class OntopusCatalogURI extends AbstractEntityIdentifier {
    public OntopusCatalogURI(String uri) {
        super(uri);
    }

    public OntopusCatalogURI(URI uri) {
        super(uri);
    }
}
