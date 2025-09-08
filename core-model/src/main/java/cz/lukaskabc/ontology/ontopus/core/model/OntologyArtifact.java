package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;

import java.util.List;

@OWLClass(iri = Vocabulary.s_c_OntologyArtifact)
public class OntologyArtifact extends PersistenceEntity {
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_title)
    private MultilingualString label;

    @OWLDataProperty(iri = Vocabulary.s_p_hasOntologyDistribution)
    private List<OntologyDistribution> distributions;
}
