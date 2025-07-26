package cz.lukaskabc.ontology.ontopus.api.model;

import java.net.URI;

public interface Ontology {
    String getName();

    URI getOntologyUri();

    URI getUri();

    String getVersionInfo();

    URI getVersionIri();
}
