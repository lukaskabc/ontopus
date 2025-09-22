package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.DatasetSeries;
import cz.lukaskabc.ontology.ontopus.core.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.model.util.SerializableImportProcessContext;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_OntologyVersionSeries)
public class VersionSeries extends DatasetSeries<VersionArtifactURI, DistributionURI, VersionSeriesURI> {
    @OWLDataProperty(iri = Vocabulary.s_p_serializedImportContext, simpleLiteral = true)
    private SerializableImportProcessContext serializableImportProcessContext;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private VersionSeriesURI identifier;

    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_hasCurrentVersion, cascade = CascadeType.MERGE)
    private VersionArtifactURI currentVersion;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_last)
    private VersionArtifactURI last;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_first)
    private VersionArtifactURI first;

    @OWLDataProperty(iri = Vocabulary.s_p_dcat_seriesMember)
    private Set<VersionArtifactURI> members;

    public VersionArtifactURI getCurrentVersion() {
        return currentVersion;
    }

    @Override
    public Set<DistributionURI> getDistributions() {
        return Set.of(); // TODO version series distributions
    }

    @Override
    public VersionArtifactURI getFirst() {
        return first;
    }

    @Override
    public VersionSeriesURI getIdentifier() {
        return identifier;
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

    public void setCurrentVersion(VersionArtifactURI currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public void setDistributions(Set<DistributionURI> distributions) {}

    @Override
    public void setFirst(VersionArtifactURI first) {
        this.first = first;
    }

    @Override
    public void setIdentifier(VersionSeriesURI identifier) {
        this.identifier = identifier;
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
}
