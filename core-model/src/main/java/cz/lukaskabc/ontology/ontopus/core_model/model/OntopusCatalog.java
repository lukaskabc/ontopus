package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Catalog;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/** A catalog of {@link VersionArtifact} served by the ontopus instance */
@OWLClass(iri = Vocabulary.s_c_OntopusCatalog)
// TODO replace distribution URI with catalog distribution URI
public class OntopusCatalog extends Catalog<DistributionURI, OntopusCatalogURI> {
    /// {@link VersionSeries}
    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_dataset)
    private Set<URI> ontologyVersionSeries = new HashSet<>();

    @Override
    public void addDistribution(DistributionURI distributionURI) {}

    public void addVersionSeries(VersionSeriesURI versionSeriesURI) {
        this.ontologyVersionSeries.add(versionSeriesURI.toURI());
    }

    @Override
    public Set<DistributionURI> getDistributions() {
        return Set.of(); // TODO catalog distributions
    }

    public Set<VersionSeriesURI> getVersionSeries() {
        return ontologyVersionSeries.stream().map(VersionSeriesURI::new).collect(Collectors.toSet());
    }

    @Override
    public boolean hasDistribution(DistributionURI distributionURI) {
        return false;
    }

    public boolean hasVersionSeries(VersionSeriesURI versionSeriesURI) {
        return this.ontologyVersionSeries.contains(versionSeriesURI.toURI());
    }

    @Override
    public void removeDistribution(DistributionURI distributionURI) {}

    public void removeVersionSeries(VersionSeriesURI versionSeriesURI) {
        this.ontologyVersionSeries.remove(versionSeriesURI.toURI());
    }

    @Override
    protected OntopusCatalogURI wrapUri(URI uri) {
        return new OntopusCatalogURI(uri);
    }
}
