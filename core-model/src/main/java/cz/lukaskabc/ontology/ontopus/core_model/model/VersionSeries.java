package cz.lukaskabc.ontology.ontopus.core_model.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core_model.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core_model.model.dcat.DatasetSeries;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.DistributionURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionArtifactURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core_model.model.util.SerializableImportProcessContext;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@OWLClass(iri = Vocabulary.s_c_VersionSeries)
public class VersionSeries extends DatasetSeries<VersionArtifactURI, DistributionURI, VersionSeriesURI> {
    @Valid @OWLDataProperty(iri = Vocabulary.s_p_serializedImportContext, simpleLiteral = true)
    private SerializableImportProcessContext serializableImportProcessContext;

    /** The newest version of the ontology */
    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_last)
    private URI last;

    /** The oldest version of the ontology */
    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_first)
    private URI first;

    @OWLObjectProperty(iri = Vocabulary.s_p_dcat_seriesMember)
    private Set<URI> members = new HashSet<>();

    @Override
    public void addDistribution(DistributionURI distributionURI) {}

    @Override
    public void addMember(VersionArtifactURI member) {
        members.add(member.toURI());
    }

    @Override
    public Set<DistributionURI> getDistributions() {
        return Set.of(); // TODO version series distributions
    }

    @Override
    public VersionArtifactURI getFirst() {
        if (first == null) {
            return null;
        }
        return new VersionArtifactURI(first);
    }

    @Override
    public VersionArtifactURI getLast() {
        if (last == null) {
            return null;
        }
        return new VersionArtifactURI(last);
    }

    @Override
    public Set<VersionArtifactURI> getMembers() {
        return members.stream().map(VersionArtifactURI::new).collect(Collectors.toSet());
    }

    public SerializableImportProcessContext getSerializableImportProcessContext() {
        return serializableImportProcessContext;
    }

    @Override
    public boolean hasDistribution(DistributionURI distributionURI) {
        return false;
    }

    @Override
    public boolean hasMember(VersionArtifactURI member) {
        return members.contains(member.toURI());
    }

    @Override
    public void removeDistribution(DistributionURI distributionURI) {}

    @Override
    public void removeMember(VersionArtifactURI member) {
        members.remove(member.toURI());
    }

    @Override
    public void setFirst(VersionArtifactURI first) {
        this.first = first.toURI();
    }

    @Override
    public void setLast(VersionArtifactURI last) {
        this.last = last.toURI();
    }

    public void setSerializableImportProcessContext(SerializableImportProcessContext serializableImportProcessContext) {
        this.serializableImportProcessContext = serializableImportProcessContext;
    }

    @Override
    protected VersionSeriesURI wrapUri(URI uri) {
        return new VersionSeriesURI(uri);
    }
}
