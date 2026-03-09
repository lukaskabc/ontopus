package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class VersionArtifactURI extends ResourceURI implements EntityIdentifier, GraphURI {
    public VersionArtifactURI(GraphURI graphURI) {
        super(graphURI.toURI());
    }

    public VersionArtifactURI(String uri) {
        super(uri);
    }

    public VersionArtifactURI(URI uri) {
        super(uri);
    }
}
