package cz.lukaskabc.ontology.ontopus.api.rest;

import org.springframework.http.MediaType;

import java.util.Set;

/** Controller capable of producing response in specified media types. */
public interface NegotiableController {
    /**
     * The media types that the controller can produce.
     *
     * @return the set of supported media types
     * @implSpec The value should be cached for fast access
     */
    Set<MediaType> getSupportedMediaTypes();
}
