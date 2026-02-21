package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class ControllerURI extends ResourceURI implements EntityIdentifier {
    public ControllerURI(String uri) {
        super(uri);
    }

    public ControllerURI(URI uri) {
        super(uri);
    }
}
