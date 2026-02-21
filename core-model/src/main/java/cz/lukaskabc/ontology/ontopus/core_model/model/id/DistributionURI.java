package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class DistributionURI extends ResourceURI implements EntityIdentifier {
    public DistributionURI(String uri) {
        super(uri);
    }

    public DistributionURI(URI uri) {
        super(uri);
    }
}
