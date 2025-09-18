package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.*;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.DatasetSeries;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import cz.lukaskabc.ontology.ontopus.core.model.util.SerializableImportProcessContext;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_OntologyVersionSeries)
public class OntologyVersionSeries extends DatasetSeries<VersionSeriesURI> {
    @OWLDataProperty(iri = Vocabulary.s_p_serializedImportContext, simpleLiteral = true)
    private SerializableImportProcessContext serializableImportProcessContext;

    @NotNull @OWLDataProperty(iri = Vocabulary.s_p_dcat_identifier, simpleLiteral = true)
    private VersionSeriesURI identifier;

    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_hasCurrentVersion, cascade = CascadeType.MERGE)
    private URI currentVersion;

    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_hasVersion, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Set<URI> versions = Set.of();

    public URI getCurrentVersion() {
        return currentVersion;
    }

    @Override
    public VersionSeriesURI getIdentifier() {
        return identifier;
    }

    public SerializableImportProcessContext getSerializableImportProcessContext() {
        return serializableImportProcessContext;
    }

    public Set<URI> getVersions() {
        return versions;
    }

    public void setCurrentVersion(URI currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public void setIdentifier(VersionSeriesURI identifier) {
        this.identifier = identifier;
    }

    public void setSerializableImportProcessContext(SerializableImportProcessContext serializableImportProcessContext) {
        this.serializableImportProcessContext = serializableImportProcessContext;
    }

    public void setVersions(Set<URI> versions) {
        this.versions = versions;
    }
}
