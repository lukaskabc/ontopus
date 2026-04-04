package cz.lukaskabc.ontology.ontopus.core.service.content_negotiation;

import cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping.ControllerDescription;
import org.springframework.http.MediaType;

/**
 * A controller candidate for producing a media type.
 *
 * @param mediaType A media type supported by the controller
 * @param controller The controller capable of producing the media type
 */
public record ControllerCandidate(MediaType mediaType, ControllerDescription controller) {}
