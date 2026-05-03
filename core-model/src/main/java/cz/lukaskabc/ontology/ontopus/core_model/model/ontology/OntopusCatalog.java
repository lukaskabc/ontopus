package cz.lukaskabc.ontology.ontopus.core_model.model.ontology;

import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.cvut.kbss.jopa.model.annotations.Types;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Agent;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.Catalog;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.OntopusCatalogURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.MappedClassTypesResolver;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/** A catalog of {@link VersionArtifact} served by the ontopus instance */
@OWLClass(iri = Vocabulary.s_c_OntopusCatalog)
public class OntopusCatalog extends Catalog<OntopusCatalogURI> {
    public static final Set<URI> TYPES = MappedClassTypesResolver.resolveTypes(OntopusCatalog.class);

    @OWLObjectProperty(iri = Vocabulary.s_i_dcat_publisher, cascade = CascadeType.ALL)
    private Agent publisher;

    @Types
    private Set<URI> types;

    /// {@link VersionSeries}
    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_dataset)
    private Set<URI> ontologyVersionSeries = new HashSet<>();

    public OntopusCatalog() {
        this.types = new HashSet<>(TYPES);
        this.types.add(Vocabulary.u_c_sioc_Document);
    }

    public void addVersionSeries(VersionSeriesURI versionSeriesURI) {
        this.ontologyVersionSeries.add(versionSeriesURI.toURI());
    }

    public Agent getPublisher() {
        return publisher;
    }

    public Set<URI> getTypes() {
        return types;
    }

    public Set<VersionSeriesURI> getVersionSeries() {
        return ontologyVersionSeries.stream().map(VersionSeriesURI::new).collect(Collectors.toUnmodifiableSet());
    }

    public boolean hasVersionSeries(VersionSeriesURI versionSeriesURI) {
        return this.ontologyVersionSeries.contains(versionSeriesURI.toURI());
    }

    public void removeVersionSeries(VersionSeriesURI versionSeriesURI) {
        this.ontologyVersionSeries.remove(versionSeriesURI.toURI());
    }

    public OntopusCatalog setPublisher(Agent publisher) {
        this.publisher = publisher;
        return this;
    }

    @Override
    protected OntopusCatalogURI wrapUri(URI uri) {
        return new OntopusCatalogURI(uri);
    }
}
