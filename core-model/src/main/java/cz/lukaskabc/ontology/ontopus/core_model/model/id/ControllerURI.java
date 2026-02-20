package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class ControllerURI extends AbstractEntityIdentifier {
    public ControllerURI(String uri) {
        super(uri);
    }

    public ControllerURI(URI uri) {
        super(uri);
    }
}
