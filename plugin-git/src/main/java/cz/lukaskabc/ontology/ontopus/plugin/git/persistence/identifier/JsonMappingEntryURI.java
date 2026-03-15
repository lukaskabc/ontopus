package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractTypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;

import java.net.URI;

public class JsonMappingEntryURI extends AbstractTypedIdentifier implements EntityIdentifier {
    public JsonMappingEntryURI(String uri) {
        super(uri);
    }

    public JsonMappingEntryURI(URI uri) {
        super(uri);
    }
}
