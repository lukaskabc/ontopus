package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import cz.lukaskabc.ontology.ontopus.core.generated.Vocabulary;
import cz.lukaskabc.ontology.ontopus.core.model.dcat.DatasetSeries;
import cz.lukaskabc.ontology.ontopus.core.model.id.VersionSeriesURI;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_OntologyVersionSeries)
public class OntologyVersionSeries extends DatasetSeries<VersionSeriesURI> {
    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_hasCurrentVersion, cascade = CascadeType.MERGE)
    private OntologyArtifact currentVersion;

    @NotNull @OWLObjectProperty(iri = Vocabulary.s_p_dcat_hasVersion, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Set<URI> versions = Set.of();

    public OntologyArtifact getCurrentVersion() {
        return currentVersion;
    }

    public Set<URI> getVersions() {
        return versions;
    }

    public void setCurrentVersion(OntologyArtifact currentVersion) {
        this.currentVersion = currentVersion;
    }

    public void setVersions(Set<URI> versions) {
        this.versions = versions;
    }
}
