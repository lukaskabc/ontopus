package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.MultilingualString;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Resource;
import java.net.URI;
import java.util.Set;

/** An ontology artifact containing a single ontology release */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifact)
public class OntologyArtifact extends Resource {
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_title)
    private MultilingualString label;

    @OWLDataProperty(iri = Vocabulary.s_p_hasOntologyDistribution)
    private Set<URI> distributions;

    public Set<URI> getDistributions() {
        return distributions;
    }

    public MultilingualString getLabel() {
        return label;
    }

    public void setDistributions(Set<URI> distributions) {
        this.distributions = distributions;
    }

    public void setLabel(MultilingualString label) {
        this.label = label;
    }
}
