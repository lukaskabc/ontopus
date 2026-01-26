package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class DistributionURI extends AbstractEntityIdentifier {
    public DistributionURI(String uri) {
        super(uri);
    }

    public DistributionURI(URI uri) {
        super(uri);
    }
}
