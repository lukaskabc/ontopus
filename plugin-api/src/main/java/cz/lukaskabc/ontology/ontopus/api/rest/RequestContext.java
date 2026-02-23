package cz.lukaskabc.ontology.ontopus.api.rest;

import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionArtifact;
import cz.lukaskabc.ontology.ontopus.core_model.model.ontology.VersionSeries;
import org.springframework.http.MediaType;

/**
 * The context of the request
 *
 * @param mediaType the media type supported by the controller
 * @param versionArtifact the version artifact to which the requested resource/ontology is related
 * @param versionSeries the version series of the version artifact
 */
public record RequestContext(MediaType mediaType, VersionArtifact versionArtifact, VersionSeries versionSeries) {}
