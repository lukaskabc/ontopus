package cz.lukaskabc.ontology.ontopus.core_model.model.mapping;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.GraphURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;

import java.net.URI;

public class ResourceInContextMapping {
    public static final String RESOURCE_IN_CONTEXT_MAPPING = "ResourceInContextMapping";
    private final URI subject;
    private final URI object;

    public ResourceInContextMapping(URI subject, URI object) {
        this.subject = subject;
        this.object = object;
    }

    public GraphURI getObject() {
        return new GraphURI(object);
    }

    public ResourceURI getSubject() {
        return new ResourceURI(subject);
    }
}
