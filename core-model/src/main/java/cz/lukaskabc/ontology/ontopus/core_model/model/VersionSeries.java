package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.DatasetSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;

import java.net.URI;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_VersionSeries)
public class VersionSeries extends DatasetSeries<VersionArtifactURI, DistributionURI, VersionSeriesURI> {
    @OWLDataProperty(iri = Vocabulary.s_p_serializedImportContext, simpleLiteral = true)
    private SerializableImportProcessContext serializableImportProcessContext;

    /** The newest version of the ontology */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_last)
    private VersionArtifactURI last;

    /** The oldest version of the ontology */
    @OWLDataProperty(iri = Vocabulary.s_p_dcat_first)
    private VersionArtifactURI first;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_seriesMember)
    private Set<VersionArtifactURI> members;

    @Override
    public Set<DistributionURI> getDistributions() {
        return Set.of(); // TODO version series distributions
    }

    @Override
    public VersionArtifactURI getFirst() {
        return first;
    }

    @Override
    public VersionArtifactURI getLast() {
        return last;
    }

    @Override
    public Set<VersionArtifactURI> getMembers() {
        return members;
    }

    public SerializableImportProcessContext getSerializableImportProcessContext() {
        return serializableImportProcessContext;
    }

    @Override
    public void setDistributions(Set<DistributionURI> distributions) {}

    @Override
    public void setFirst(VersionArtifactURI first) {
        this.first = first;
    }

    @Override
    public void setLast(VersionArtifactURI last) {
        this.last = last;
    }

    @Override
    public void setMembers(Set<VersionArtifactURI> members) {
        this.members = members;
    }

    public void setSerializableImportProcessContext(SerializableImportProcessContext serializableImportProcessContext) {
        this.serializableImportProcessContext = serializableImportProcessContext;
    }

    @Override
    protected VersionSeriesURI wrapUri(URI uri) {
        return new VersionSeriesURI(uri);
    }
}
