package cz.lukaskabc.ontology.ontopus.core.model.id;

import java.net.URI;

public class VersionArtifactURI extends AbstractEntityIdentifier {
    public VersionArtifactURI(String uri) {
        super(uri);
    }

    public VersionArtifactURI(URI uri) {
        super(uri);
    }
}
