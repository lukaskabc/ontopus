package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Dataset;
import cz.lukaskabc.ontology.ontopus.core.model.id.ArtifactURI;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

/** An ontology artifact containing a single ontology release */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifact)
public class OntologyArtifact extends Dataset<ArtifactURI> {

    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_ontologyIdentifier)
    private URI ontologyIdentifier;

    public URI getOntologyIdentifier() {
        return ontologyIdentifier;
    }

    public void setOntologyIdentifier(URI ontologyIdentifier) {
        this.ontologyIdentifier = ontologyIdentifier;
    }
}
