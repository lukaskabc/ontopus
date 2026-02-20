package cz.lukaskabc.ontology.ontopus.core_model.model.ontology;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Dataset;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/** An ontology artifact containing a single ontology version release */
@OWLClass(iri = Vocabulary.s_c_VersionArtifact)
public class VersionArtifact extends Dataset<DistributionURI, VersionArtifactURI> {

    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_distribution)
    private Set<URI> distributions = new HashSet<>();

    @Override
    public void addDistribution(DistributionURI distributionURI) {
        this.distributions.add(distributionURI.toURI());
    }

    @Override
    public Set<DistributionURI> getDistributions() {
        return distributions.stream().map(DistributionURI::new).collect(Collectors.toSet());
    }

    @Override
    public boolean hasDistribution(DistributionURI distributionURI) {
        return this.distributions.contains(distributionURI.toURI());
    }

    @Override
    public void removeDistribution(DistributionURI distributionURI) {
        this.distributions.remove(distributionURI.toURI());
    }

    @Override
    protected VersionArtifactURI wrapUri(URI uri) {
        return new VersionArtifactURI(uri);
    }
}
