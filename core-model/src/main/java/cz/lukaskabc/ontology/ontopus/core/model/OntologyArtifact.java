package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

/** An ontology artifact containing a single ontology release */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifact)
public class OntologyArtifact extends Resource {

    @NotEmpty @OWLDataProperty(iri = Vocabulary.s_p_hasOntologyDistribution)
    private Set<URI> distributions;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_ontologyIdentifier)
    private URI ontologyIdentifier;

    public Set<URI> getDistributions() {
        return distributions;
    }

    public URI getOntologyIdentifier() {
        return ontologyIdentifier;
    }

    public void setDistributions(Set<URI> distributions) {
        this.distributions = distributions;
    }

    public void setOntologyIdentifier(URI ontologyIdentifier) {
        this.ontologyIdentifier = ontologyIdentifier;
    }
}
