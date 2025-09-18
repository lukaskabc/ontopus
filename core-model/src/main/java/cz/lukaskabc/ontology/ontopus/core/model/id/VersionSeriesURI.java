package cz.lukaskabc.ontology.ontopus.core.model.id;

import java.net.URI;

public class VersionSeriesURI extends AbstractEntityIdentifier {
    public VersionSeriesURI(String uri) {
        super(uri);
    }

    public VersionSeriesURI(URI uri) {
        super(uri);
    }
}
