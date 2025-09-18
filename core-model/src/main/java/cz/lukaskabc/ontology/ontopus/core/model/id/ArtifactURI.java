package cz.lukaskabc.ontology.ontopus.core.model.id;

import java.net.URI;

public class ArtifactURI extends AbstractEntityIdentifier {
    public ArtifactURI(String uri) {
        super(uri);
    }

    public ArtifactURI(URI uri) {
        super(uri);
    }
}
