package cz.lukaskabc.ontology.ontopus.plugin.git.persistence.identifier;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.AbstractTypedIdentifier;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.EntityIdentifier;

import java.net.URI;

public class JsonPointerConditionURI extends AbstractTypedIdentifier implements EntityIdentifier {
    public JsonPointerConditionURI(String uri) {
        super(uri);
    }

    public JsonPointerConditionURI(URI uri) {
        super(uri);
    }
}
