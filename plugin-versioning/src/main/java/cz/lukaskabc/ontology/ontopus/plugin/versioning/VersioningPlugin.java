package cz.lukaskabc.ontology.ontopus.plugin.versioning;

import cz.lukaskabc.ontology.ontopus.api.Plugin;

import java.net.URI;
import java.util.Set;

/** OntoPuS plugin providing versioning services for date based and semantic versioning. */
public class VersioningPlugin implements Plugin {
    static final Set<URI> VERSION_EXAMPLES = Set.of(
            URI.create("http://purl.org/dc/terms/hasVersion"),
            URI.create("http://purl.org/pav/version"),
            URI.create("http://schema.org/schemaVersion"),
            URI.create("http://www.w3.org/ns/dcat#version"),
            URI.create("http://www.w3.org/2002/07/owl#versionInfo"));
    static final Set<URI> VERSION_IRI_EXAMPLES = Set.of(URI.create("http://www.w3.org/2002/07/owl#versionIRI"));
}

// TODO: merge versioning plugin into the core? or rename required plugins to
// modules? that would be more confusing
