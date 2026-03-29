package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class ControllerDescriptionURI extends ResourceURI implements EntityIdentifier {
    public ControllerDescriptionURI(String uri) {
        super(uri);
    }

    public ControllerDescriptionURI(URI uri) {
        super(uri);
    }
}
