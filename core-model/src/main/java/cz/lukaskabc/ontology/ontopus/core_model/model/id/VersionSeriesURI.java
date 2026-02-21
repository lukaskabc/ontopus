package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class VersionSeriesURI extends ResourceURI implements EntityIdentifier {
    public VersionSeriesURI(String uri) {
        super(uri);
    }

    public VersionSeriesURI(URI uri) {
        super(uri);
    }
}
