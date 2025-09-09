package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Dataset;
import java.net.URI;
import java.util.List;

/** A catalog of {@link OntologyArtifact} served by the ontopus instance */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifactCatalog)
public class OntologyArtifactCatalog extends Dataset {
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_homepage)
    private URI homepage;

    @OWLObjectProperty(iri = Vocabulary.s_p_containsOntologyArtifact, fetch = FetchType.LAZY)
    private List<URI> ontologyArtifacts;
}
