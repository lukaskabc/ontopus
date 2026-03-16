package cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping;

import cz.cvut.kbss.jopa.model.annotations.Individual;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;

import java.net.URI;

/** The type of a resource mapping */
public enum MappingType {
    /** The mapping is for the main ontology document */
    @Individual(iri = Vocabulary.s_i_OntologyDocument)
    ONTOLOGY_DOCUMENT(Vocabulary.u_i_OntologyDocument),
    /** The mapping is for all resources defined in the ontology document */
    @Individual(iri = Vocabulary.s_i_OntologyResource)
    RESOURCE(Vocabulary.u_i_OntologyResource);

    private final URI uri;

    MappingType(URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }
}
