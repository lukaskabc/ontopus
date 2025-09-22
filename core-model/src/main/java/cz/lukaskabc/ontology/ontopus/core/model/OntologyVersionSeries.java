package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.DatasetSeries;
import cz.lukaskabc.ontology.ontopus.core.model.id.ArtifactURI;
import cz.lukaskabc.ontology.ontopus.core.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.model.util.SerializableImportProcessContext;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_OntologyVersionSeries)
public class OntologyVersionSeries extends DatasetSeries<ArtifactURI, DistributionURI, VersionSeriesURI> {
    @OWLDataProperty(iri = Vocabulary.s_p_serializedImportContext, simpleLiteral = true)
    private SerializableImportProcessContext serializableImportProcessContext;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private VersionSeriesURI identifier;

    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_hasCurrentVersion, cascade = CascadeType.MERGE)
    private ArtifactURI currentVersion;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_last)
    private ArtifactURI last;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_first)
    private ArtifactURI first;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_seriesMember)
    private Set<ArtifactURI> members;

    public ArtifactURI getCurrentVersion() {
        return currentVersion;
    }

    @Override
    public Set<DistributionURI> getDistributions() {
        return Set.of(); // TODO version series distributions
    }

    @Override
    public ArtifactURI getFirst() {
        return first;
    }

    @Override
    public VersionSeriesURI getIdentifier() {
        return identifier;
    }

    @Override
    public ArtifactURI getLast() {
        return last;
    }

    @Override
    public Set<ArtifactURI> getMembers() {
        return members;
    }

    public SerializableImportProcessContext getSerializableImportProcessContext() {
        return serializableImportProcessContext;
    }

    public void setCurrentVersion(ArtifactURI currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public void setDistributions(Set<DistributionURI> distributions) {}

    @Override
    public void setFirst(ArtifactURI first) {
        this.first = first;
    }

    @Override
    public void setIdentifier(VersionSeriesURI identifier) {
        this.identifier = identifier;
    }

    @Override
    public void setLast(ArtifactURI last) {
        this.last = last;
    }

    @Override
    public void setMembers(Set<ArtifactURI> members) {
        this.members = members;
    }

    public void setSerializableImportProcessContext(SerializableImportProcessContext serializableImportProcessContext) {
        this.serializableImportProcessContext = serializableImportProcessContext;
    }
}
