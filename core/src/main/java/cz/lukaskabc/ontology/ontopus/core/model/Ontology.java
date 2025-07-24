package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.vocabulary.OWL;
import cz.lukaskabc.ontology.ontopus.core.util.OWLVocabulary;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.URI;

@Getter
@Setter
@Accessors(chain = true)
@OWLClass(iri = OWL.ONTOLOGY)
public class Ontology {
    @Id
    private URI iri;
    @OWLDataProperty(iri = OWLVocabulary.VERSION_IRI)
    private URI versionIri;
    @OWLDataProperty(iri = OWLVocabulary.VERSION_INFO)
    private String versionInfo;
}
