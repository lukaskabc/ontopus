package cz.lukaskabc.ontology.ontopus.core.service.resource_fallback;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.Consumer;

/** Applies a change to the {@link UriComponentsBuilder} to fall back the URI to an alternative */
public interface ResourceRequestFallback extends Consumer<UriComponentsBuilder> {}
