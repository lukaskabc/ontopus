package cz.lukaskabc.ontology.ontopus.api.service.core;

import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import org.jspecify.annotations.NullMarked;

/**
 * Generates an IRI that should be used for a temporary database GRAPH while importing a new ontology. The context is
 * only valid until the server is restarted.
 */
@NullMarked
public interface TemporaryContextGenerator {
    TemporaryContextURI generate();
}
