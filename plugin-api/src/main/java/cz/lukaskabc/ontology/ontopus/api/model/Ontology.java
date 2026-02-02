package cz.lukaskabc.ontology.ontopus.api.model;

import org.jspecify.annotations.NullUnmarked;

import java.net.URI;

@NullUnmarked
public interface Ontology {
    String getName();

    URI getOntologyUri();

    URI getUri();

    String getVersionInfo();

    URI getVersionIri();

    void setName(String name);

    void setOntologyUri(URI ontologyUri);

    void setUri(URI uri);

    void setVersionInfo(String versionInfo);

    void setVersionIri(URI versionIri);
}
