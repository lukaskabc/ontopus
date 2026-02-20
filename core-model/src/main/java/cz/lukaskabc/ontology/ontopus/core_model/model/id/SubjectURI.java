package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class SubjectURI extends AbstractTypedIdentifier {

    public SubjectURI(String uri) {
        super(uri);
    }

    public SubjectURI(URI uri) {
        super(uri);
    }
}
