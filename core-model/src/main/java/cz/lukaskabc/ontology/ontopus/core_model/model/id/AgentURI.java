package cz.lukaskabc.ontology.ontopus.core_model.model.id;

import java.net.URI;

public class AgentURI extends AbstractTypedIdentifier {
    public AgentURI(String uri) {
        super(uri);
    }

    public AgentURI(URI uri) {
        super(uri);
    }
}
