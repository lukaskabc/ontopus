package cz.lukaskabc.ontology.ontopus.api.rest;

import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.ResourceURI;
import org.springframework.http.MediaType;

/**
 * The request for a resource. The resource could be a resource in an ontology or request for the whole ontology.
 *
 * @param mediaType the media type supported by the controller
 * @param requestedURI the URI of the requested resource
 * @param ontologyVersionUri the URI of the graph in which the requested resource is located and the version IRI of the
 *     ontology
 */
public record OntopusRequest(MediaType mediaType, ResourceURI requestedURI, OntologyVersionURI ontologyVersionUri) {}
