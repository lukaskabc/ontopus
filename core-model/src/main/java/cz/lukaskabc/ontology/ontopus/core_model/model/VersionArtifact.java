package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Dataset;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/** An ontology artifact containing a single ontology version release */
@OWLClass(iri = Vocabulary.s_c_VersionArtifact)
public class VersionArtifact extends Dataset<DistributionURI, VersionArtifactURI> {

    // TODO resolve uri, identifier and ontology identifier conflict

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier)
    private VersionArtifactURI identifier;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_distribution)
    private Set<DistributionURI> distributions;

    @Override
    public Set<DistributionURI> getDistributions() {
        return distributions;
    }

    @Override
    public VersionArtifactURI getIdentifier() {
        return identifier;
    }

    @Override
    public void setDistributions(Set<DistributionURI> distributions) {
        this.distributions = distributions;
    }

    @Override
    public void setIdentifier(VersionArtifactURI identifier) {
        this.identifier = identifier;
    }
}
