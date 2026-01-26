package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class VersionSeriesURI extends AbstractEntityIdentifier {
    public VersionSeriesURI(String uri) {
        super(uri);
    }

    public VersionSeriesURI(URI uri) {
        super(uri);
    }
}
