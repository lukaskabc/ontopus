package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.lukaskabc.ontology.ontopus.generated.Vocabulary;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

@OWLClass(iri = Vocabulary.s_c_mod_SemanticArtefact)
public class Ontology extends PersistenceEntity implements cz.lukaskabc.ontology.ontopus.api.model.Ontology {
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_i_mod_title, simpleLiteral = true)
    @NotEmpty private String name;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_i_mod_URI, simpleLiteral = true)
    @NotNull private URI ontologyUri;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_i_mod_versionInfo, simpleLiteral = true)
    @NotEmpty private String versionInfo;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_i_mod_versionIRI, simpleLiteral = true)
    @NotNull private URI versionIri;

    // TODO: remove MOD ontology as it is only a draft and make own

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URI getOntologyUri() {
        return ontologyUri;
    }

    @Override
    public String getVersionInfo() {
        return versionInfo;
    }

    @Override
    public URI getVersionIri() {
        return versionIri;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setOntologyUri(URI ontologyUri) {
        this.ontologyUri = ontologyUri;
    }

    @Override
    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    @Override
    public void setVersionIri(URI versionIri) {
        this.versionIri = versionIri;
    }
}
