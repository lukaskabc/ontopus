package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class OntopusCatalogURI extends ResourceURI implements EntityIdentifier {
    public OntopusCatalogURI(String uri) {
        super(uri);
    }

    public OntopusCatalogURI(URI uri) {
        super(uri);
    }
}
