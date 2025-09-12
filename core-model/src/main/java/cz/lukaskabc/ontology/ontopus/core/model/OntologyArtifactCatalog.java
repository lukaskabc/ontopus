package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Dataset;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

/** A catalog of {@link OntologyArtifact} served by the ontopus instance */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifactCatalog)
public class OntologyArtifactCatalog extends Dataset {
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_homepage)
    private URI homepage;

    @OWLObjectProperty(iri = Vocabulary.s_p_containsOntologyArtifact)
    private Set<URI> ontologyArtifacts;

    public URI getHomepage() {
        return homepage;
    }

    public Set<URI> getOntologyArtifacts() {
        return ontologyArtifacts;
    }

    public void setHomepage(URI homepage) {
        this.homepage = homepage;
    }

    public void setOntologyArtifacts(Set<URI> ontologyArtifacts) {
        this.ontologyArtifacts = ontologyArtifacts;
    }
}
