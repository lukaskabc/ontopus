package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.core.model.id.TemporaryContextURI;
import java.net.URI;
import java.util.Set;

/** Service capable of resolving the identifier of an ontology. */
public interface OntologyIdentifierResolvingService {
    /**
     * Resolves possible identifier(s) of an ontology in the specified {@code databaseContext}
     *
     * @param databaseContext The database graph containing the ontology
     * @return Found identifier(s)
     */
    Set<URI> resolve(TemporaryContextURI databaseContext);
}
