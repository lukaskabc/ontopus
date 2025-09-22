package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Catalog;
import cz.lukaskabc.ontology.ontopus.core.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/** A catalog of {@link VersionArtifact} served by the ontopus instance */
@OWLClass(iri = Vocabulary.s_c_OntopusCatalog)
// TODO replace distribution URI with catalog distribution URI
public class OntopusCatalog extends Catalog<DistributionURI, OntopusCatalogURI> {
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private OntopusCatalogURI identifier;
    /// {@link VersionSeries}
    @OWLDataProperty(iri = Vocabulary.s_p_hasOntology)
    private Set<VersionSeriesURI> ontologySeries;

    @Override
    public Set<DistributionURI> getDistributions() {
        return Set.of(); // TODO catalog distributions
    }

    @Override
    public OntopusCatalogURI getIdentifier() {
        return identifier;
    }

    public Set<VersionSeriesURI> getOntologySeries() {
        return ontologySeries;
    }

    @Override
    public void setDistributions(Set<DistributionURI> distributions) {}

    @Override
    public void setIdentifier(OntopusCatalogURI identifier) {
        this.identifier = identifier;
    }

    public void setOntologySeries(Set<VersionSeriesURI> ontologySeries) {
        this.ontologySeries = ontologySeries;
    }
}
