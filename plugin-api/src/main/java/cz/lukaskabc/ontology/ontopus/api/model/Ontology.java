package cz.lukaskabc.ontology.ontopus.api.model;

import java.net.URI;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public interface Ontology {
    String getName();

    URI getOntologyUri();

    URI getUri();

    String getVersionInfo();

    URI getVersionIri();
}
