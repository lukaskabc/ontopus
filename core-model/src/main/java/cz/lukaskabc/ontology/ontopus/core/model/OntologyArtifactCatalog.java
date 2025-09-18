package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.Catalog;
import cz.lukaskabc.ontology.ontopus.core.model.id.ArtifactCatalogURI;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

/** A catalog of {@link OntologyArtifact} served by the ontopus instance */
@OWLClass(iri = Vocabulary.s_c_OntologyArtifactCatalog)
public class OntologyArtifactCatalog extends Catalog<ArtifactCatalogURI> {
    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private ArtifactCatalogURI identifier;
    /// {@link OntologyVersionSeries}
    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_dataset)
    private Set<URI> ontologySeries;

    @Override
    public ArtifactCatalogURI getIdentifier() {
        return identifier;
    }

    public Set<URI> getOntologySeries() {
        return ontologySeries;
    }

    @Override
    public void setIdentifier(ArtifactCatalogURI identifier) {
        this.identifier = identifier;
    }

    public void setOntologySeries(Set<URI> ontologySeries) {
        this.ontologySeries = ontologySeries;
    }
}
