package cz.lukaskabc.ontology.ontopus.core.model;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import cz.cvut.kbss.jopa.vocabulary.DC;
import cz.lukaskabc.ontology.ontopus.generated.Vocabulary;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@OWLClass(iri = Ontology.Meta.s_TYPE)
public class Ontology extends PersistenceEntity {
    @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = DC.Terms.TITLE, simpleLiteral = true)
    private String name;

    @NotNull @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_ONTOLOGY_URI, simpleLiteral = true)
    private URI ontologyUri;

    @NotEmpty @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_VERSION_INFO)
    private String versionInfo;

    @NotNull @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Meta.s_VERSION_IRI)
    private URI versionIri;

    public static class Meta {
        public static final String s_TYPE = Vocabulary.s_c_mod_SemanticArtefact;
        public static final URI TYPE = URI.create(s_TYPE);
        public static final String s_ONTOLOGY_URI = Vocabulary.s_i_mod_URI;
        public static final URI ONTOLOGY_URI = URI.create(s_ONTOLOGY_URI);
        public static final String s_NAME = Vocabulary.s_i_mod_title;
        public static final URI NAME = URI.create(s_NAME);
        public static final String s_VERSION_INFO = Vocabulary.s_i_mod_versionInfo;
        public static final URI VERSION_INFO = URI.create(s_VERSION_INFO);
        public static final String s_VERSION_IRI = Vocabulary.s_i_mod_versionIRI;
        public static final URI VERSION_IRI = URI.create(s_VERSION_IRI);

        private Meta() {
            throw new AssertionError();
        }
    }
}
