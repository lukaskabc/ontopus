package cz.lukaskabc.ontology.ontopus.api.service;

import cz.lukaskabc.ontology.ontopus.api.model.Ontology;
import java.net.URI;

/**
 * Object capable of filling {@link cz.lukaskabc.ontology.ontopus.api.model.Ontology} using imported ontology data in a
 * temporary context
 */
public interface OntologyBuilder {
    /**
     * Accepts partially filled ontology object and fills or updates additional data in it.
     *
     * @param partialOntology possibly a completely empty object to fill.
     * @param databaseContext the graph with the imported ontology
     */
    void build(Ontology partialOntology, URI databaseContext);
}
