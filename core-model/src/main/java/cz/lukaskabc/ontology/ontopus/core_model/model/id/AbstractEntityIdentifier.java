package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public abstract class AbstractEntityIdentifier extends AbstractTypedIdentifier {
    public AbstractEntityIdentifier(String uri) {
        super(uri);
    }

    public AbstractEntityIdentifier(URI uri) {
        super(uri);
    }
}
