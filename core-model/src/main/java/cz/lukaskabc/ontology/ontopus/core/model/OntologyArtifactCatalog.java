package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Catalog;
import cz.lukaskabc.ontology.ontopus.core.model.id.ArtifactCatalogURI;
import cz.lukaskabc.ontology.ontopus.core.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/** A catalog of {@link OntologyArtifact} served by the ontopus instance */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifactCatalog)
// TODO replace distribution URI with catalog distribution URI
public class OntologyArtifactCatalog extends Catalog<DistributionURI, ArtifactCatalogURI> {
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private ArtifactCatalogURI identifier;
    /// {@link OntologyVersionSeries}
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_dataset)
    private Set<VersionSeriesURI> ontologySeries;

    @Override
    public Set<DistributionURI> getDistributions() {
        return Set.of(); // TODO catalog distributions
    }

    @Override
    public ArtifactCatalogURI getIdentifier() {
        return identifier;
    }

    public Set<VersionSeriesURI> getOntologySeries() {
        return ontologySeries;
    }

    @Override
    public void setDistributions(Set<DistributionURI> distributions) {}

    @Override
    public void setIdentifier(ArtifactCatalogURI identifier) {
        this.identifier = identifier;
    }

    public void setOntologySeries(Set<VersionSeriesURI> ontologySeries) {
        this.ontologySeries = ontologySeries;
    }
}
