package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.Convert;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.converter.id.ArtifactUriConverter;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Dataset;
import cz.lukaskabc.ontology.ontopus.core.model.id.ArtifactURI;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

/** An ontology artifact containing a single ontology release */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifact)
public class OntologyArtifact extends Dataset<ArtifactURI> {

    @Convert(converter = ArtifactUriConverter.class)
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier)
    private ArtifactURI identifier;

    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_ontologyIdentifier)
    private URI ontologyIdentifier;

    @Override
    public ArtifactURI getIdentifier() {
        return identifier;
    }

    public URI getOntologyIdentifier() {
        return ontologyIdentifier;
    }

    @Override
    public void setIdentifier(ArtifactURI identifier) {
        this.identifier = identifier;
    }

    public void setOntologyIdentifier(URI ontologyIdentifier) {
        this.ontologyIdentifier = ontologyIdentifier;
    }
}
