package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Catalog;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;

import java.net.URI;
import java.util.Set;

/** A catalog of {@link VersionArtifact} served by the ontopus instance */
@OWLClass(iri = Vocabulary.s_c_OntopusCatalog)
// TODO replace distribution URI with catalog distribution URI
public class OntopusCatalog extends Catalog<DistributionURI, OntopusCatalogURI> {
    /// {@link VersionSeries}
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_dataset)
    private Set<VersionSeriesURI> ontologySeries;

    @Override
    public Set<DistributionURI> getDistributions() {
        return Set.of(); // TODO catalog distributions
    }

    public Set<VersionSeriesURI> getOntologySeries() {
        return ontologySeries;
    }

    @Override
    public void setDistributions(Set<DistributionURI> distributions) {}

    public void setOntologySeries(Set<VersionSeriesURI> ontologySeries) {
        this.ontologySeries = ontologySeries;
    }

    @Override
    protected OntopusCatalogURI wrapUri(URI uri) {
        return new OntopusCatalogURI(uri);
    }
}
