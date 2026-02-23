package cz.lukaskabc.ontology.ontopus.core_model.model.request_mapping;

import cz.cvut.kbss.jopa.model.annotations.Individual;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;

/** The type of a resource mapping */
public enum MappingType {
    /** The mapping is for the main ontology document */
    @Individual(iri = Vocabulary.s_i_OntologyDocument)
    ONTOLOGY_DOCUMENT,
    /** The mapping is for all resources defined in the ontology document */
    @Individual(iri = Vocabulary.s_i_OntologyResource)
    RESOURCE
}
