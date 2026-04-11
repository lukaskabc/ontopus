package cz.lukaskabc.ontology.ontopus.core_model.model.ontology;

import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Dataset;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntologyVersionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/** An ontology artifact containing a single ontology version release */
@OWLClass(iri = Vocabulary.s_c_VersionArtifact)
public class VersionArtifact extends Dataset<DistributionURI, VersionArtifactURI> {
    @OWLObjectProperty(iri = Vocabulary.s_p_ontologyVersionIdentifier)
    private URI versionUri;

    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_distribution, fetch = FetchType.EAGER)
    private Set<URI> distributions = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_hasPrefixDeclaration, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PrefixDeclaration> prefixDeclarations = new HashSet<>();

    @Override
    public void addDistribution(DistributionURI distributionURI) {
        this.distributions.add(distributionURI.toURI());
    }

    public void addPrefixDeclaration(PrefixDeclaration prefixDeclaration) {
        prefixDeclarations.add(prefixDeclaration);
    }

    @Override
    public Set<DistributionURI> getDistributions() {
        return distributions.stream().map(DistributionURI::new).collect(Collectors.toUnmodifiableSet());
    }

    public Set<PrefixDeclaration> getPrefixDeclarations() {
        return prefixDeclarations;
    }

    public VersionSeriesURI getSeries() {
        return new VersionSeriesURI(getSeriesURI());
    }

    public OntologyVersionURI getVersionUri() {
        if (versionUri == null) {
            return null;
        }
        return new OntologyVersionURI(versionUri);
    }

    @Override
    public boolean hasDistribution(DistributionURI distributionURI) {
        return this.distributions.contains(distributionURI.toURI());
    }

    @Override
    public void removeDistribution(DistributionURI distributionURI) {
        this.distributions.remove(distributionURI.toURI());
    }

    public void setSeries(VersionSeriesURI series) {
        setSeriesURI(series.toURI());
    }

    public void setVersionUri(OntologyVersionURI versionUri) {
        this.versionUri = versionUri.toURI();
    }

    @Override
    protected VersionArtifactURI wrapUri(URI uri) {
        return new VersionArtifactURI(uri);
    }
}
