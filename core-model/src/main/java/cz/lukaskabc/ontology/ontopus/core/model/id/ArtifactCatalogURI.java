package cz.lukaskabc.ontology.ontopus.core.model.id;

import java.net.URI;

public class ArtifactCatalogURI extends AbstractEntityIdentifier {
    public ArtifactCatalogURI(String uri) {
        super(uri);
    }

    public ArtifactCatalogURI(URI uri) {
        super(uri);
    }
}
