package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class UserURI extends ResourceURI implements EntityIdentifier {
    public UserURI(String uri) {
        super(uri);
    }

    public UserURI(URI uri) {
        super(uri);
    }
}
